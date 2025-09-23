package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.domain.Reply;
import com.green.chakak.chakak.community.repository.PostJpaRepository;
import com.green.chakak.chakak.community.repository.ReplyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

	private final ReplyJpaRepository replyRepository;
	private final PostJpaRepository postRepository;
	private final UserJpaRepository userJpaRepository;


	@Transactional
	public ReplyResponse.CreateDTO createReply(Long postId, ReplyRequest.CreateDTO request, LoginUser loginUser) {
		if (loginUser == null) {
			throw new Exception403("로그인 후 댓글을 작성할 수 있습니다.");
		}

		Post post = postRepository.findActiveByIdJoinUser(postId)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

		User user = userJpaRepository.findById(loginUser.getId())
				.orElseThrow(() -> new Exception404("사용자 정보를 찾을 수 없습니다."));

		Reply reply = request.toEntity(post, user);
		reply.setContent(reply.getContent().trim());
		Reply savedReply = replyRepository.save(reply);
		post.increaseReplyCount();


		return new ReplyResponse.CreateDTO(savedReply);
	}


	@Transactional(readOnly = true)
	public List<PostResponse.ReplyDTO> getRepliesByPost(Long postId, LoginUser loginUser) {
		if (loginUser == null) {
			throw new Exception403("로그인 후 댓글을 확인할 수 있습니다.");
		}

		postRepository.findActiveByIdJoinUser(postId)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

		List<Reply> replies = replyRepository.findActiveRepliesByPostIdJoinUser(postId);

		return replies.stream()
				.map(reply -> new PostResponse.ReplyDTO(reply, loginUser.getId()))
				.collect(Collectors.toList());
	}


	@Transactional
	public ReplyResponse.UpdateDTO updateReply(Long replyId, ReplyRequest.UpdateDTO request, LoginUser loginUser) {
		Reply reply = checkOwnership(replyId, loginUser);
		reply.updateContent(request.getContent());
		return new ReplyResponse.UpdateDTO(reply);
	}


	@Transactional
	public void deleteReply(Long replyId, LoginUser loginUser) {
		Reply reply = checkOwnership(replyId, loginUser);
		reply.getPost().decreaseReplyCount();
		reply.deleteReply();
	}


	@Transactional(readOnly = true)
	public List<PostResponse.ReplyDTO> getUserReplies(Long userId, LoginUser loginUser) {
		User user = userJpaRepository.findById(userId)
				.orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

		List<Reply> userReplies = replyRepository.findActiveRepliesByUserIdJoinPost(userId);

		return userReplies.stream()
				.map(reply -> new PostResponse.ReplyDTO(reply, loginUser != null ? loginUser.getId() : null))
				.collect(Collectors.toList());
	}


	@Transactional(readOnly = true)
	public List<PostResponse.ReplyDTO> getUserALLReplies(Long userId, LoginUser loginUser) {
		User user = userJpaRepository.findById(userId)
				.orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

		List<Reply> userReplies = replyRepository.findAllRepliesByPostIdJoinUser(userId);

		return userReplies.stream()
				.map(reply -> new PostResponse.ReplyDTO(reply, loginUser != null ? loginUser.getId() : null))
				.collect(Collectors.toList());
	}


	@Transactional(readOnly = true)
	public List<PostResponse.ReplyDTO> getMyReplies(LoginUser loginUser) {
		if (loginUser == null) {
			throw new Exception403("로그인이 필요합니다.");
		}
		return getUserReplies(loginUser.getId(), loginUser);
	}


	private Reply checkOwnership(Long replyId, LoginUser loginUser) {
		if (loginUser == null) {
			throw new Exception403("로그인이 필요합니다.");
		}

		Reply reply = replyRepository.findActiveReplyByIdJoinUser(replyId)
				.orElseThrow(() -> new Exception404("댓글을 찾을 수 없습니다."));

		if (!reply.isOwner(loginUser.getId())) {
			throw new Exception403("댓글 작성자만 수정/삭제할 수 있습니다.");
		}

		return reply;
	}
}