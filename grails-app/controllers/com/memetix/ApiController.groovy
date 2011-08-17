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
        def responseObj = [:]
        def shortUrls = asList(params.shortUrl)

        if(shortUrls.size()>0) {
            def futures = []
            for(shortUrl in shortUrls) {
                futures.add(unshortenService.unshortenFuture(shortUrl))
            }
            def eUrls = []
            for(future in futures) {
                eUrls.add(future.get())
            }
            responseObj.status_code = 200
            responseObj.status_txt = "OK"
            responseObj.data = [:]
            responseObj.data.expand = eUrls

        } else {
            responseObj.status_code = 400
            responseObj.status_txt = "Please Provide 1 or more URLs in shortUrl paramter"
        }
        
        if(params.callback) {
            render "${params.callback}(${responseObj as JSON})"
        } else {
            render responseObj as JSON
        }
    }
    private asList(orig) {
        if(orig)
            return new HashSet([orig].flatten())
        return []
    }
}
