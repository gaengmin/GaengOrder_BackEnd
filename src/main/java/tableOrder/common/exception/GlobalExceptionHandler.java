package tableOrder.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 2. NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointer(NullPointerException e) {
        return ResponseEntity.status(500).body("Null 값 오류가 발생했습니다.");
    }

    //접근제한 Exception
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleIllegalArgument(AccessDeniedException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 3. 여러 예외를 한 번에 처리
    @ExceptionHandler({IndexOutOfBoundsException.class, ArrayIndexOutOfBoundsException.class})
    public ResponseEntity<String> handleIndex(Exception e) {
        return ResponseEntity.status(400).body("인덱스 오류가 발생했습니다.");
    }

    // 4. 모든 예외의 최종 처리 (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
    }
}