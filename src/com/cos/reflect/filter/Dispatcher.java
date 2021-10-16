package com.cos.reflect.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.reflect.anno.RequestMapping;
import com.cos.reflect.controller.UserController;

public class Dispatcher implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		System.out.println("컨텍스트패스 : " + request.getContextPath()); // 프로젝트 시작주소
		System.out.println("식별자주소 : " + request.getRequestURI()); // 끝주소
		System.out.println("전체주소 : " + request.getRequestURL()); // 전체주소

		String endPoint = request.getRequestURI().replaceAll(request.getContextPath(), "");
		System.out.println("엔드포인트 : " + endPoint);

		UserController userController = new UserController();
		Method[] methods = userController.getClass().getDeclaredMethods();

		for (Method method : methods) { // 리플렉션한 메서드 개수만큼 순회함

			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			RequestMapping requestMapping = (RequestMapping) annotation;
			System.out.println("requestMapping : " + requestMapping.value());
			if (requestMapping.value().equals(endPoint)) {
		
				try {
					
					String path = (String) method.invoke(userController);
					
					RequestDispatcher dis = request.getRequestDispatcher(path);
					dis.forward(request, response);
					break; // 더 이상 메서드를 리플렉션 할 필요 없어서 빠져나감.
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}

		}

	}


}