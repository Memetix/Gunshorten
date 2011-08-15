// Place your Spring DSL code here
import java.util.concurrent.Executors
beans = {
    executorService(  grails.plugin.executor.PersistenceContextExecutorWrapper ) { bean->
        bean.destroyMethod = 'destroy' //keep this destroy method so it can try and clean up nicely
        persistenceInterceptor = ref("redisDatastorePersistenceInterceptor")
        //this can be whatever from Executors (don't write your own and pre-optimize)
        executor = Executors.newFixedThreadPool(100) 
    }
}