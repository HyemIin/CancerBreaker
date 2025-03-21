package com.example.cancerbreaker.board.repository

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardRepository : JpaRepository<Board,Long>{
    fun findByCategory(category: BoardCategory): List<Board>?

    @Query(
        value = """
            SELECT * 
            FROM board 
            WHERE MATCH(title, content) AGAINST(:keyword IN BOOLEAN MODE)
            AND category = 'ANTI_CANCER';
        """,
        nativeQuery = true
    )
    fun search(@Param("keyword") keyword: String): List<Board>
}