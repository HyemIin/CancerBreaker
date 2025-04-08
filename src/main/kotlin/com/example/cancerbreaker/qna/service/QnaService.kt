package com.example.cancerbreaker.qna.service

import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.member.entity.User
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
    // QnA 생성
    @Transactional
    fun executeCreateQna(qnaCreateRequest: QnaCreateRequest): Result<QnaCreateResponse> {
        return createQnaInternal(
            request = qnaCreateRequest,
            getUserId = { sessionUtil.getCurrentUserId() },
            findUser = { id -> userRepository.findByIdOrNull(id) },
            saveQna = { qna -> qnaRepository.save(qna) }
        )
    }

    private fun createQnaInternal(
        request: QnaCreateRequest,
        getUserId: () -> Long,
        findUser: (Long) -> User?,
        saveQna: (Qna) -> Qna
    ): Result<QnaCreateResponse> {
        return runCatching {
            val userId = getUserId()
            val user = findUser(userId) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
            val qna = Qna(request.title, request.content, user, request.isPublic)
            val savedQna = saveQna(qna)
            QnaCreateResponse().fromEntity(savedQna)
        }
    }

    // QnA 전체 리스트 조회
    @Transactional(readOnly = true)
    fun executeGetQnaList(): Result<List<QnaGetResponse>> {
        return getQnaListInternal(
            findAllQnas = { qnaRepository.findAll() }
        )
    }

    private fun getQnaListInternal(
        findAllQnas: () -> List<Qna>
    ): Result<List<QnaGetResponse>> {
        return runCatching {
            val qnaList = findAllQnas()
            qnaList.map { QnaGetResponse().fromEntity(it) }
        }
    }

    // QnA 특정 게시글 조회
    @Transactional(readOnly = true)
    fun getQnaById(qnaId: Long): Result<QnaGetResponse> {
        return getQnaByIdInternal(
            qnaId = qnaId,
            findQna = { id -> qnaRepository.findByIdOrNull(id) }
        )
    }

    private fun getQnaByIdInternal(
        qnaId: Long,
        findQna: (Long) -> Qna?
    ): Result<QnaGetResponse> {
        return runCatching {
            val qna = findQna(qnaId) ?: throw IllegalArgumentException("QNA 게시글을 찾을 수 없습니다.")
            QnaGetResponse().fromEntity(qna)
        }
    }

    // QnA 게시글 수정
    @Transactional
    fun executeEditQna(qnaId: Long, qnaEditRequest: QnaEditRequest): Result<QnaEditResponse> {
        return editQnaInternal(
            qnaId = qnaId,
            request = qnaEditRequest,
            getUserId = { sessionUtil.getCurrentUserId() },
            findQna = { id -> qnaRepository.findByIdOrNull(id) }
        )
    }

    private fun editQnaInternal(
        qnaId: Long,
        request: QnaEditRequest,
        getUserId: () -> Long,
        findQna: (Long) -> Qna?
    ): Result<QnaEditResponse> {
        return runCatching {
            val userId = getUserId()
            val qna = findQna(qnaId) ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            require (userId == qna.user.id) {
                throw IllegalArgumentException("당사자만 수정할 수 있습니다.")
            }
            qna.updateQna(request)
            QnaEditResponse().fromEntity(qna)
        }
    }

    // QnA 게시글 삭제
    @Transactional
    fun executeDeleteQna(qnaId: Long): Result<String> {
        return deleteQnaInternal(
            qnaId = qnaId,
            getUserId = { sessionUtil.getCurrentUserId() },
            findQna = { id -> qnaRepository.findByIdOrNull(id) },
            deleteQna = { qna -> qnaRepository.delete(qna) }
        )
    }

    private fun deleteQnaInternal(
        qnaId: Long,
        getUserId: () -> Long,
        findQna: (Long) -> Qna?,
        deleteQna: (Qna) -> Unit
    ): Result<String> {
        return runCatching {
            val userId = getUserId()
            val qna = findQna(qnaId) ?: throw IllegalArgumentException("QNA 게시글을 찾을 수 없습니다.")
            require (userId == qna.user.id) {
                throw IllegalArgumentException("당사자만 삭제할 수 있습니다.")
            }
            deleteQna(qna)
            "게시글 삭제 완료"
        }
    }
}