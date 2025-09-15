package com.airbng.controller.chat;

import com.airbng.common.response.BaseResponse;
import com.airbng.domain.chat.Conversation;
import com.airbng.dto.chat.PeerProfileDto;
import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.chat.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/chat/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * 대화방 생성 or 조회 (내가 peerId와의 방이 없으면 생성)
     * POST /chat/conversations/{peerId}
     */
    @PostMapping("/{peerId}")
    public BaseResponse<Conversation> getOrCreate(@PathVariable long peerId,
                                                  Authentication auth) {
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        if (me == peerId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "self conversation not allowed");
        }
        return new BaseResponse<>(conversationService.getOrCreate(me, peerId));
    }

    /**
     * convId로 조회 (없으면 404)
     * GET /chat/conversations/{convId}
     */
    @GetMapping("/{convId}")
    public BaseResponse<Conversation> get(@PathVariable String convId) {
        Conversation c = conversationService.findById(convId);
        if (c == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "conversation not found");
        }
        return new BaseResponse<>(c);
    }

    /**
     * 생성 없이 존재만 조회 (peer 기준)
     * GET /chat/conversations/by-peer/{peerId}
     */
    @GetMapping("/by-peer/{peerId}")
    public BaseResponse<Conversation> getByPeer(@PathVariable long peerId, Authentication auth) {
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        String convId = conversationService.makeConvId(me, peerId);
        Conversation c = conversationService.findById(convId);
        if (c == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "conversation not found");
        }
        return new BaseResponse<>(c);
    }

    /**
     * 내 기준 상대 userId 반환
     * GET /chat/conversations/{convId}/peer
     */
    @GetMapping("/{convId}/peer")
    public BaseResponse<PeerProfileDto> getPeer(@PathVariable String convId, Authentication auth) {
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        conversationService.assertMember(convId, me);
        return new BaseResponse<>(conversationService.getPeerProfile(convId, me));
    }
}
