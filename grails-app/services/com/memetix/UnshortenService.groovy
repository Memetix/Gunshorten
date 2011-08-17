package com.memetix
import java.util.concurrent.Executors
import java.util.concurrent.Callable

class UnshortenService {

    static transactional = false
    static urlRegex = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:\/~\+#]*[\w\-\@?^=%&amp;\/~\+#])/
    def connectTimeoutInMilliseconds = 1000
    def readTimeoutInMilliseconds = 1000
    def maxNumRedirects = 3
    def redisService
    def jobPool = Executors.newFixedThreadPool(100) 
    
    
    def unshortenFuture(shortUrl) {
        return jobPool.submit({return unshorten(shortUrl)} as Callable)
    }
    
    /**
     * expandUrlsInText()                         
     *
     * Takes in a text string - for example, a Tweet - that may or may not contain any URLs
     * 
     * If any URLs are found, this method attempts to unshorten it, and replaces the original shortened
     * URL with the expanded URL text. 
     *
     * @param  text A string of text that may or may not contain URLs         
     * @return The original input string, with all shortened URLs replaced by their unshortened versions
     *
     * @version     1.0.1   2011.05.18                              
     * @since       1.0     2011.05.17
     */
    def expandUrlsInText(text) {
        log.debug "expandUrlsInText(): ${text}"
        def expandText = text
        def urls = expandText?.findAll(urlRegex) as Set
        for(url in urls) {
            def fullUrl = unshorten(url)?.fullUrl
            if(fullUrl&&url!=fullUrl) {
                log.debug "expandUrlsInText - Replacing Url - [${url} : ${fullUrl}]"
                expandText = expandText.replaceAll(url,fullUrl)
            }
        }
        log.debug "expandUrlsInText() RETURNING: ${expandText}"
        return expandText
    }

    /**
     * unshorten()                         
     *
     * Takes a shortened string and gives you back the expanded string
     * 
     * If the linkString parameter matches the expanded version, then the normalized version of the original linkString is returned.
     * If the linkString parameter is an invalid URL, then location is null
     * 
     * This method will first attempt to load the expanded URL from the LRUCache. If the linkString matches the expanded version, the
     * result is not stored in the cache.
     *
     * @param  linkString A string representing a shortened URL (i.e. http://bit.ly/jkD0Qr)           
     * @return A Map representing the properties of the expanded, original URL
     *  
     *      returnMap.shortUrl = the original URL
     *      returnMap.fullUrl = the unshortened URL
     *      returnMap.cached = A boolean that is true if the shortenUrl was retrieved from the cache, and false if HTTP rigamarole was required
     *      returnMap.status = A String, the state of the URL and one of the following possible values:
     *                          "unshortened" = successfully unshortened
     *                          "not_shortened" = URL was already expanded, i.e. no redirect required
     *                          "404_not_found" = No document at this URL, returned 404 Error
     *                          "timed_out" = Destination server or Shortener API did not respond within the timeouts specified by Config.groovy
     *                          "invalid" = Poorly formed, or non-existent URL
     *                          "unknown" = Something else happened
     *                          
     * @version     1.0.3   2011.05.20                              
     * @since       1.0     2011.05.17  
     *               
     */
    def unshorten(linkString) {
        return unshorten(linkString,0)
    }
    def unshorten(linkString,redirectCount) {
        //sprintln linkString
        log.debug "Unshortening Link ${linkString}"
        def link = linkString?.trim()
        
        if(link
           &&!link.toLowerCase().contains("http://")
           &&!link.toLowerCase().contains("https://")
           &&!link.toLowerCase().contains("ftp://")) {
            link = "http://${link}"
        }
        
        def location = getCache(link.toString())
        
        if(location.fullUrl) {
            location.cached = true
            log.debug "Fetched Link from cache - [${link} : ${location}]"
            return location
        } else {
            location.shortUrl = link
        }
        
        try {
            def url = new URL(link)
            URLConnection urlc = url.openConnection();
            urlc.setRequestMethod("HEAD");
            urlc.setInstanceFollowRedirects( false );
            urlc.setConnectTimeout(connectTimeoutInMilliseconds);
            urlc.setReadTimeout(readTimeoutInMilliseconds);
            urlc.connect()
            def responseCode = urlc.getResponseCode();
            if(responseCode>300&&responseCode<304) {
                location.fullUrl = urlc.getHeaderField("Location");
                if(location?.fullUrl?.contains(url?.getHost())) {
                    location.status = UrlStatus.REDIRECTED.toString()
                } else if(location.fullUrl
                       &&!location?.fullUrl?.toLowerCase().contains("http://")
                       &&!location?.fullUrl?.toLowerCase().contains("https://")
                       &&!location?.fullUrl?.toLowerCase().contains("ftp://")) {
                    location.fullUrl = "${url?.getAuthority()}${location?.fullUrl}"
                    location.status = UrlStatus.REDIRECTED
                } else {
                    location.status = UrlStatus.UNSHORTENED
                }
                if(redirectCount<maxNumRedirects)
                    location.fullUrl = unshorten(location.fullUrl,++redirectCount)?.fullUrl?.trim()
            } else if(responseCode==404) {
                location.fullUrl = link
                location.status = UrlStatus.NOT_FOUND
            } else {
                location.fullUrl = link
                location.status = UrlStatus.NOT_SHORTENED
            }
        } catch(SocketTimeoutException e) {
            log.debug "Timeout Unshortening URL ${linkString}: ${e}"
            location.fullUrl = link
            location.status = UrlStatus.TIMED_OUT
        } catch (Exception e) {
            location.fullUrl = link
            location.status = UrlStatus.INVALID
            log.debug "Error Unshortening URL ${linkString}: ${e}"
        }
        
        if(location&&location.status!=UrlStatus.TIMED_OUT) {
            log.debug "Caching [${link} : ${location}]"
            putCache(location)
        } else {
            location.fullUrl = link
            if(!location.status)
                location.status = UrlStatus.UNKNOWN
            log.debug "Not Caching [${link} : ${location}]"
        }
        location.cached = false
        location.status = location.status.toString()
        return location
    }
    
    def getCache(key) {
        //println key
        def location = [:]
        location.fullUrl = redisService.hget(key.toString(),"fullUrl")
        location.shortUrl = redisService.hget(key.toString(),"shortUrl")
        location.status = redisService.hget(key.toString(),"status")
        location.cached = redisService.hget(key.toString(),"cached")
        redisService.expire(key.toString(),3600)
        return location
    }
    def putCache(location) {
        //println location
        redisService.hset(location.shortUrl.toString(),"fullUrl",location.fullUrl.toString())
        redisService.hset(location.shortUrl.toString(),"shortUrl",location.shortUrl.toString())
        redisService.hset(location.shortUrl.toString(),"status",location.status.toString())
        redisService.hset(location.shortUrl.toString(),"cached",location.cached.toString())
        redisService.expire(location.shortUrl.toString(),3600)
    }
}

enum UrlStatus {UNSHORTENED,NOT_FOUND,REDIRECTED,TIMED_OUT,INVALID,NOT_SHORTENED,UNKNOWN}