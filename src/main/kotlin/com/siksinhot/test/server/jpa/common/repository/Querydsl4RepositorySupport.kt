package com.siksinhot.test.server.jpa.common.repository

import com.siksinhot.test.common.KeyGenerator
import com.siksinhot.test.exception.ErrorCode
import com.siksinhot.test.extensions.stackTraceString
import com.siksinhot.test.server.jpa.common.entity.BaseEntity
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.siksinhot.test.exception.TwinException
import io.ktor.http.*
import mu.KotlinLogging
import java.time.Clock
import java.time.LocalDateTime
import javax.persistence.EntityManager
import kotlin.reflect.KClass

val logger = KotlinLogging.logger(Querydsl4RepositorySupport::class.java.name)

/**
 * Querydsl 4.x 버전에 맞춘 Querydsl 지원 라이브러리
 *
 */
@Suppress("UNCHECKED_CAST")
open class Querydsl4RepositorySupport<T>(val entityManager: EntityManager, val domainClass: KClass<Any>) {
    var queryFactory: JPAQueryFactory
    var builder: PathBuilder<*>

    //  lateinit var querydsl: Querydsl
    val batchSize = 300

    init {
        val path: EntityPath<T> = EntityPathBase<T>(domainClass.java as Class<T>, "entity")
        builder = PathBuilder(path.type, path.metadata)
//    querydsl = Querydsl(
//      entityManager,
//      builder
//    )
        queryFactory = JPAQueryFactory(entityManager)
    }


    //ref : https://stackoverflow.com/questions/13072378/how-can-i-convert-a-spring-data-sort-to-a-querydsl-orderspecifier
    open fun ordable(sort: Sort): MutableList<OrderSpecifier<*>> {

        val specifiers: MutableList<OrderSpecifier<*>> = mutableListOf()

        for (o in sort) {

            specifiers.add(
                OrderSpecifier<Comparable<Any>>(
                    if (o?.isAscending == true) Order.ASC else Order.DESC,
                    builder[o?.property] as Expression<Comparable<Any>>
                )
            )
        }

        return specifiers
    }

    fun select(): JPAQuery<T> {
        //ToDo : Unchecked cast : JPAQuery<*>! to JPAQuery<T> ??? fix it
        return queryFactory.query() as JPAQuery<T>
    }

    fun select(expr: Expression<T>?): JPAQuery<T> {
        return queryFactory.select(expr) as JPAQuery<T>
    }

    fun selectFrom(from: EntityPath<T>?): JPAQuery<T> {
        return queryFactory.selectFrom(from) as JPAQuery<T>
    }

    fun selectOne(): JPAQuery<T> {
        return queryFactory.selectOne() as JPAQuery<T>
    }

    fun count(from: EntityPath<T>?): Long {
        return selectFrom(from).fetchCount()
    }

    fun toPage(from: EntityPath<T>?, pageRequest: Pageable, data: List<T>): Page {
        val totalCount = count(from)
        val page = pageRequest.pageNumber
        val pageSize = pageRequest.pageSize
        val totalPages = totalCount/pageSize + if ((totalCount%pageSize).toInt() ==0) 0 else 1
        return Page(
            totalPages.toInt(),
            totalCount,
            data as List<Any>,
      page == 1,
            if (page.toLong() == totalPages) true else false,
    page < totalPages.toInt(),
  page > 1,
        )
    }
    
    fun insert(entity: T): T {
        if (entity is BaseEntity) {
            entity.id = KeyGenerator.uuid()
            entity.regDatetime = LocalDateTime.now(Clock.systemUTC())
            entity.modDatetime = entity.regDatetime
        }
        entityManager.persist(entity)
        return entity
    }

    fun update(entity: T): T {
        if (entity is BaseEntity) {
            entity.modDatetime = LocalDateTime.now(Clock.systemUTC())
        }
        return entityManager.merge(entity)
    }

    fun delete(entity: T) {
        entityManager.remove(entity)
    }

    inline fun transaction(body: () -> Unit) {
        try {
            entityManager.transaction.begin()
            body()
            entityManager.transaction.commit()
        } catch (ex: Exception) {
            logger.error("trnsaction error", ex)
            entityManager.transaction.rollback()
            if (ex.stackTraceString.indexOf("SQLIntegrityConstraintViolationException") >= 0) {
                throw TwinException(ErrorCode.E10007, ex)
            } else {
                throw TwinException(
                    ErrorCode.E10006,
                    ex,
                    HttpStatusCode.InternalServerError.value,
                    "error occurs in db orm transaction"
                )
            }
        }
    }

    /*
  fun applyPagination(
    pageable: Pageable,
    jpaQuery: JPAQuery<*>
  ): Page<T> {
      val content: List<T> = querydsl.applyPagination(
          pageable,
          jpaQuery
      ).fetch() as List<T>
      return PageableExecutionUtils.getPage(content, pageable) { jpaQuery.fetchCount() }
  }

  fun applyPagination(
      pageable: Pageable,
      jpaQuery: JPAQuery<*>,
      countQuery: JPAQuery<*>
  ): Page<T> {
      val content: List<T> = querydsl.applyPagination(
          pageable,
          jpaQuery
      ).fetch() as List<T>
      return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchCount() }
  }
   */
}
