package com.memetix

import grails.converters.*

class ApiController {
    def unshortenService
    
    def index = {
        
        if(params.shortUrl) {
            [urlObj: unshortenService.unshorten(params.shortUrl)]
        }
            
    }
    
    def expand = {
        def futures = []
        for(shortUrl in asList(params.shortUrl)) {
            final futureUrl = shortUrl
            futures.add(callAsync {
                try {
                    return unshortenService.unshorten(futureUrl)
                } catch(Exception e) {
                    log.error "${e}"
                }
            })
        }
        def eUrls = []
        for(future in futures) {
            eUrls.add(future.get())
        }
        println eUrls
            
        def responseObj = [:]
        responseObj.status_code = 200
        responseObj.status_txt = "OK"
        responseObj.data = [:]
        responseObj.data.expand = eUrls
        
        if(params.callback) {
            render "${params.callback}(${responseObj as JSON})"
        } else {
            render responseObj as JSON
        }
    }
    private asList(orig) {
        return new HashSet([orig].flatten())
    }
}
