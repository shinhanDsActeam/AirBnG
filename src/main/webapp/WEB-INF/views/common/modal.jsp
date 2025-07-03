<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- 기본 확인 모달 --%>
<div id="confirm-modal" class="modal-overlay hidden">
    <div class="modal">
        <div class="modal-content">
            <div class="modal-icon warning">?</div>
            <div class="modal-title">확인이 필요합니다</div>
            <div class="modal-message">
                정말로 진행하시겠습니까?
            </div>
        </div>
        <div class="modal-buttons">
            <button class="modal-btn" onclick="ModalUtils.hideModal('confirm-modal')">아니요</button>
            <button class="modal-btn" onclick="ModalUtils.confirmAction('confirm-modal')">확인</button>
        </div>
    </div>
</div>

<%-- 기본 성공 모달 --%>
<div id="success-modal" class="modal-overlay hidden">
    <div class="modal">
        <div class="modal-content">
            <div class="modal-icon success-rotate">✓</div>
            <div class="modal-title">성공</div>
            <div class="modal-text">
                작업이 성공적으로 완료되었습니다.
            </div>
        </div>
        <div class="modal-buttons">
            <button class="modal-btn single" onclick="ModalUtils.confirmAction('success-modal')">확인</button>
        </div>
    </div>
</div>

<%-- 기본 에러 모달 --%>
<div id="error-modal" class="modal-overlay hidden">
    <div class="modal">
        <div class="modal-content">
            <div class="modal-icon error-shake">!</div>
            <div class="modal-title">오류</div>
            <div class="modal-text">
                처리 중 오류가 발생했습니다.
            </div>
        </div>
        <div class="modal-buttons">
            <button class="modal-btn single" onclick="ModalUtils.confirmAction('error-modal')">확인</button>
        </div>
    </div>
</div>

<%-- 기본 정보 모달 --%>
<div id="info-modal" class="modal-overlay hidden">
    <div class="modal">
        <div class="modal-content">
            <div class="modal-icon info">i</div>
            <div class="modal-title">알림</div>
            <div class="modal-text">
                안내 메시지가 표시됩니다.
            </div>
        </div>
        <div class="modal-buttons">
            <button class="modal-btn single" onclick="ModalUtils.confirmAction('info-modal')">확인</button>
        </div>
    </div>
</div>

<!-- 경고 모달 -->
<div id="warning-modal" class="modal-overlay hidden">
    <div class="modal">
        <div class="modal-content">
            <div class="modal-icon warning-pulse">!</div>
            <div class="modal-title">요청이 너무 많습니다</div>
            <div class="modal-text">잠시 후 다시 시도해주세요.</div>
        </div>
        <div class="modal-buttons">
            <button class="modal-btn single" onclick="ModalUtils.confirmAction('warning-modal')">확인</button>
        </div>
    </div>
</div>