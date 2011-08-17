package com.memetix

import grails.converters.*

class StatsController {
    def redisService
    def unshortenService
    
    def index = {
        def max = params.max ?: 100
        def urls = []
        for(url in redisService.zrevrange("urls:gets",0,max as int)) {
            def shortUrl = unshortenService.hydrateUrlMap(url)
            shortUrl.count = redisService.zscore("urls:gets",url)
            urls.add(shortUrl)
        } 
        [shortUrls:urls]
    }
}
