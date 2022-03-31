package ca.bc.gov.data.local.dao

import ca.bc.gov.data.datasource.local.dao.CommentDao
import ca.bc.gov.data.local.BaseDataBaseTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Pinakin Kansara
 */
class CommentDaoTest : BaseDataBaseTest() {

    private lateinit var commentDao: CommentDao

    override fun onCreate() {
        commentDao = db.getCommentDao()
    }

    @Test
    fun insertAndGetComment() = runBlocking {
        val id = commentDao.insert(getComment())
        assertTrue(id> 0)
    }

    override fun tearDown() {
        // no implementation required
    }
}
