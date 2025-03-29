package com.example.cancerbreaker.qna.service

import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.member.repository.UserRepository
import com.example.cancerbreaker.qna.dto.request.QnaCreateRequest
import com.example.cancerbreaker.qna.dto.request.QnaEditRequest
import com.example.cancerbreaker.qna.dto.response.QnaCreateResponse
import com.example.cancerbreaker.qna.dto.response.QnaEditResponse
import com.example.cancerbreaker.qna.dto.response.QnaGetResponse
import com.example.cancerbreaker.qna.entity.Qna
import com.example.cancerbreaker.qna.repository.QnaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QnaService(
    private val qnaRepository: QnaRepository,
    private val userRepository: UserRepository,
    private val sessionUtil: SessionUtil
) {
    // qna 생성
    @Transactional
    fun createQna(qnaCreateRequest: QnaCreateRequest) : QnaCreateResponse {
        val userId = sessionUtil.getCurrentUserId()
        val user = userRepository.findByIdOrNull(userId)!!
        val qna = qnaRepository.save(Qna(qnaCreateRequest.title,qnaCreateRequest.content,user))
        return QnaCreateResponse().fromEntity(qna)
    }
    // qna 전체 리스트 조회
    @Transactional(readOnly = true)
    fun getQnaList(): List<QnaGetResponse>? {
        val qnaList = qnaRepository.findAll()
        return qnaList.map { QnaGetResponse().fromEntity(it) }
    }

    // QnA 특정 게시글 조회
    @Transactional(readOnly = true)
    fun getQnaById(qnaId: Long): QnaGetResponse {
        val qna = qnaRepository.findByIdOrNull(qnaId)
        return QnaGetResponse().fromEntity(qna!!)
    }
    // QnA 게시글 수정
    @Transactional
    fun editQna(qnaId: Long, qnaEditRequest: QnaEditRequest) : QnaEditResponse {
        val userId = sessionUtil.getCurrentUserId()
        val qna = qnaRepository.findByIdOrNull(qnaId)
        when (userId) {
            qna!!.user.id -> qna.updateQna(qnaEditRequest)
            else -> throw IllegalArgumentException("당사자만 수정할 수 있습니다.")
        }
        return QnaEditResponse().fromEntity(qna)
    }
    // QnA 게시글 삭제
    @Transactional
    fun deleteQna(qnaId: Long) {
        val userId = sessionUtil.getCurrentUserId()
        val qna = qnaRepository.findByIdOrNull(qnaId)
        when (userId) {
            qna!!.user.id -> qnaRepository.delete(qna)
            else -> throw IllegalArgumentException("당사자만 삭제할 수 있습니다.")
        }
    }
}