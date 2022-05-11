package org.prgms.jpa.order;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 이거 잘 알아놓자
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
	// TODO ApiResponse<T>의 구조에 대해서 자세히 파악하기
	private int statusCode;
	private T data;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime serverDateTime;

	public ApiResponse(int statusCode, T data) {
		this.statusCode = statusCode;
		this.data = data;
		this.serverDateTime = LocalDateTime.now();
	}

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(200, data);
	}

	public static <T> ApiResponse<T> fail(int statusCode, T data) {
		return new ApiResponse<>(statusCode, data);
	}
}
