package com.lineacademy.fridgemanagerspring.controller;

import com.lineacademy.fridgemanagerspring.domain.user.User;
import com.lineacademy.fridgemanagerspring.dto.user.request.CreateUserRequest;
import com.lineacademy.fridgemanagerspring.dto.user.request.LoginRequest;
import com.lineacademy.fridgemanagerspring.dto.user.response.UserResponse;
import com.lineacademy.fridgemanagerspring.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// DTO (schema) : String으로 도착한 문자열을 객체화 하여 각 요소를 저장하는 일 => Mapping

@RestController    // 이 클래스가 웹서비스를 할 때 이용되는 컨트롤러임을 명시
@RequestMapping("/users")    // /users라는 주소로 Request가 오면 컨트롤러에 도달
@RequiredArgsConstructor    // 매개변수 생성자를 자동으로 생성해주는 어노테이션
// final 필드나 @NonNull 필드가 붙은 것들을 매개변수로 함
public class UserController {
    // 멤버변수
    private final UserService userService;   // Java에서는 객체를 만들어야 실행이 가능

    // 멤버메서드
    @PostMapping("/create")     // class의 매핑정보인 "/users" 뒤에 "/create"가 붙고, POST 방식이면 이 메서드 실행
    // ResponseEntity<T> : Spring-Boot Web Service에서 응답을 정의하 제네릭 타입
    // T 자리에는 response.body(실제 내용이 기록되는 편지지) 의 타입이 들어가야 함
    public ResponseEntity<Map<String, Object>> createUser(
            // Spring-Boot에서는 컨트롤러의 메서드를 실행할 때,
            // 자동으로 req.body 값이 매개변수로 들어옴

            // @Valid 는 이 매개변수에 대해 검증 절차를 실행할 것이고, 실패하면 GlobalExceptionHandler로 에러를 던질 것이며
            // @RequestBody 는 이 매개변수에 request.body 내용을 넣어줘
            @Valid @RequestBody CreateUserRequest request
    ) {
        try {
            // 서비스에 request를 그대로 넘겨서, 생성 요청을 할 것이고
            // 서비스는 생성이 끝난 결과(생성 '된' User 객체)를 리턴하게 만들 것임
            User user = userService.createUser(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "성공적으로 회원가입 되었습니다.",
                            "data", UserResponse.from(user)
                    ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_EXISTS_EMAIL"))
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "message", "이미 가입된 이메일입니다."
                        ));
            if (e.getMessage().equals("ALREADY_EXISTS_NICKNAME"))
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "message", "이미 사용 중인 닉네임입니다."
                        ));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "서버 에러가 발생했습니다."
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        try {
            // 1. 사용자가 입력해온 값을 DB에서 조회해서 있는지 확인
            User user = userService.login(request);

            // 2. 토큰을 생성해서 response 전달
        } catch () {

        }
    }
}
