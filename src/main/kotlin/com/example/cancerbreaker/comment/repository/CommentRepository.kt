package com.example.cancerbreaker.comment.repository

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment,Long>{

    fun findAllByBoard(board: Board?): List<Comment>
}