package com.project.Project.auth.filter;

import com.project.Project.auth.authentication.JwtAuthentication;
import com.project.Project.auth.dto.MemberDto;
import com.project.Project.auth.enums.MemberRole;
import com.project.Project.auth.exception.BasicAuthException;
import com.project.Project.auth.handler.BasicAuthFailureHandler;
import com.project.Project.domain.auth.Role;
import com.project.Project.domain.member.Member;
import com.project.Project.domain.member.ProfileImage;
import com.project.Project.exception.ErrorCode;
import com.project.Project.serializer.member.MemberSerializer;
import com.project.Project.service.member.MemberService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CustomBasicAuthFilter extends BasicAuthenticationFilter {

    private MemberService memberService;


    private boolean ignoreFailure;
    private AuthenticationEntryPoint authenticationEntryPoint;

    public CustomBasicAuthFilter(AuthenticationManager authenticationManager, MemberService memberService, BasicAuthFailureHandler basicAuthFailureHandler) {
        super(authenticationManager);
        this.memberService = memberService;
    }

    public CustomBasicAuthFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint, MemberService memberService, BasicAuthFailureHandler basicAuthFailureHandler) {
        super(authenticationManager, authenticationEntryPoint);
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.memberService = memberService;
    }

    private Member mockMember(Long id) {
        return this.memberService.findById(id).orElseGet(() -> Member.builder() // temp user
                .reviewList(new ArrayList<>())
                .favoriteBuildingList(new ArrayList<>())
                .reviewLikeList(new ArrayList<>())
                .name("하품하는 망아지")
                .email("swa07016@khu.ac.kr")
                .roles(Arrays.asList(Role.builder()
                        .memberRole(MemberRole.USER).build()))
                .refreshToken("mockingMember")
                .profileImage(ProfileImage.builder().url("https://d2ykyi5jl9muoc.cloudfront.net/profile-images/blue-smile_eyes-d_mouth.png").build())
                .build()
        );
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String keyId = request.getHeader("mocking");
            if (keyId != null) {
                try {
                    Long mockMemberId = Long.parseLong(keyId);
                    Member member = this.mockMember(mockMemberId);
                    MemberDto memberDto = MemberSerializer.toDto(member);
                    Authentication auth = getAuthentication(memberDto, member, request, response);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    throw new BasicAuthException(ErrorCode.INVALID_BASIC_AUTH);
                }
            }
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            this.logger.debug("Failed to process authentication request", ex);
            if (this.ignoreFailure) {
                filterChain.doFilter(request, response);
            } else {
                this.authenticationEntryPoint.commence(request, response, ex);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }


    private Authentication getAuthentication(MemberDto memberDto, Member member, HttpServletRequest request, HttpServletResponse response) {
        return new JwtAuthentication(
                member.getRoles(),
                memberDto, member,
                member.getRefreshToken(),
                request, response);
    }
}
