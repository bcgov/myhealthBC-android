package ca.bc.gov.repository

import ca.bc.gov.data.datasource.local.cache.ApplicationCache
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 27,May,2022
 */
class CacheRepository @Inject constructor() {

    fun isProtectiveWordAdded(): Boolean {
        return ApplicationCache.protectiveWordAdded
    }

    fun updateProtectiveWordAdded(isProtectiveWordAdded: Boolean) {
        ApplicationCache.protectiveWordAdded = isProtectiveWordAdded
    }
}
