package com.memetix

import org.codehaus.groovy.grails.commons.*

class MaintenanceJob {
    def startDelay = ConfigurationHolder.config.gunshorten.cache.maintenance.startDelay
    def timeout = ConfigurationHolder.config.gunshorten.cache.maintenance.timeout
    def unshortenService
    def group = "Maintenance"
    def concurrent = false
    def sessionRequired = false

    def execute() {
        try {
            def success = unshortenService.pruneCache(System.currentTimeMillis())
            log.debug "Finished Pruning Cache: ${success}"
        } catch(Exception ex) {
            log.eerror "Error Pruning Cache ${ex}"
        }
        
    }
}
