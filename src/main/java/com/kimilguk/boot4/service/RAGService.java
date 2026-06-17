// AI 기반 RAG(Retrieval-Augmented Generation) 때문에 외부에 생성한 서비스파일
// chatClient 객체에서 제미나이 systemInstruction 사용처럼 문서기반 RAG를사용할 수 있는 코드를 알려줘. 라고 코파일럿AI에 요청한 결과(아래)
// indexDocuments에 프로젝트 최상위(루트) 폴더의 README.md 파일 내용을 읽어 인덱싱 하도록 코드를 추가해줘. 라고 코파일럿AI에 요청한 결과(아래)
// 내부의 파일 대신에 외부 URL 경로의 파일을 불러올 수 있는 방법을 알려줘. 라고 코파일럿AI에 요청한 결과(아래)
package com.kimilguk.boot4.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ai.chat.client.ChatClient; // 이미 사용 중이신 것과 동일
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 단순 메모리 벡터 스토어를 이용한 RAG 서비스 예시.
 */
@Service
public class RAGService {
	private final ChatClient chatClient;
	private final List<DocumentVector> store = new ArrayList<>(); // 메모리 스토어
	public RAGService(ChatClient.Builder chatBuilder) {
		this.chatClient = chatBuilder.build();
	}
	// 문서 인덱싱 (앱 시작시 또는 관리 API로 호출)
	// 입력으로 받은 documents 외에 프로젝트 최상위(현재 작업 디렉터리) README.md를 자동으로 읽어 포함합니다.
	public void indexDocuments(List<String> documents) {
		store.clear();
		// 복사본 리스트로 작업: null 안전 처리
		List<String> docsToIndex = new ArrayList<>();
		if (documents != null) {
			docsToIndex.addAll(documents);
		}
		// 외부 URL에서 파일 읽기 시도
	    try {
	    	List<String> externalUrl = new ArrayList<>();
	        externalUrl.add(0, "https://www.global.ac.kr/user/nd80491.do"); // 학부소개
	        externalUrl.add(1, "https://www.global.ac.kr/user/nd62080.do"); // 교수진소개
	        externalUrl.add(2, "https://www.global.ac.kr/user/nd62019.do"); // 교육과정
	        externalUrl.add(3, "https://www.global.ac.kr/user/nd50916.do"); // 민간자격증
	        externalUrl.add(4, "https://www.global.ac.kr/user/nd33386.do"); // AI콘텐츠(연계전공)
	        RestTemplate restTemplate = new RestTemplate();
	        for (int i = 0; i < externalUrl.size(); i++) {
				String url = externalUrl.get(i);
				String urlContent = restTemplate.getForObject(url, String.class);   
		        if (urlContent != null && !urlContent.isBlank()) {
		            docsToIndex.add(i, urlContent);
		        }
			}
	    } catch (Exception e) {
	        System.err.println("외부 URL 파일 로드 실패: " + e.getMessage());
	        e.printStackTrace();
	    }
		// 1) 프로젝트 루트(작업 디렉터리)에 있는 README.md 파일 읽기 시도
	    /*
		try {
			Path readmePath = Paths.get(System.getProperty("user.dir")).resolve("README.md");
			if (Files.exists(readmePath) && Files.isRegularFile(readmePath)) {
				String readmeContent = Files.readString(readmePath, StandardCharsets.UTF_8);
				if (readmeContent != null && !readmeContent.isBlank()) {
					// README를 문서 리스트의 앞에 추가(원하면 끝에 추가 가능)
					docsToIndex.add(0, readmeContent);
				}
			} else {
				// 만약 애플리케이션이 실행되는 위치와 프로젝트 루트가 달라서 찾지 못할 수 있습니다.
				// 클래스패스에 포함된 경우를 대비한 폴백(선택사항)
				try {
					var is = this.getClass().getClassLoader().getResourceAsStream("README.md");
					if (is != null) {
						String readmeContent = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
						if (!readmeContent.isBlank()) {
							docsToIndex.add(0, readmeContent);
						}
					}
				} catch (Exception ignored) {
					// 폴백 실패 시 무시
				}
			}
		} catch (IOException e) {
			// 읽기 실패 로깅(개발용) - 프로덕션에서는 로거를 사용하세요.
			e.printStackTrace();
		}
		*/
		// 2) 문서들을 인덱싱(임베딩 생성 및 store에 추가)
		for (int i = 0; i < docsToIndex.size(); i++) {
			String doc = docsToIndex.get(i);
			// 필요시 긴 문서는 청크 분할 후 각각 인덱싱하도록 확장 가능 (아래 설명 참조)
			double[] embedding = embedText(doc); // 실제 임베딩 클라이언트로 교체하세요.
			store.add(new DocumentVector("doc-" + i, doc, embedding));
		}
	}

	// 쿼리 답변 (RAG 핵심)
	public String answer(String userQuery, int topK) {
		indexDocuments(null); // 문서 인덱싱 (실제 사용에서는 앱 시작 시 한 번만 호출하거나 관리 API로 분리하는 것이 좋습니다.)
		double[] qEmbedding = embedText(userQuery);
		// 1) 유사도 계산 및 topK 선택(코사인 유사도 사용 초기값 상위 3개 결과만)
		List<DocumentVector> topDocs = store.stream()
				.map(dv -> new AbstractMap.SimpleEntry<>(dv, cosineSimilarity(qEmbedding, dv.embedding)))
				.sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())).limit(topK).map(Map.Entry::getKey)
				.collect(Collectors.toList());
		// 2) system instruction + documents 포함해서 프롬프트 구성
		StringBuilder systemBuilder = new StringBuilder();
		systemBuilder.append("앞으로의 모든 답변은 반드시 질문한 언어와 동일한 언어로 해줘. 당신은 제공된 문서를 기반으로 답변하는 챗봇입니다. 다음 문서를 참고하여 질문에 답변하세요. ");
		systemBuilder.append("문서에 답이 없으면 모른다고 하고 환각 증상이 없다고 말하세요. ");
		systemBuilder.append("\n\nDocuments:\n");
		for (int i = 0; i < topDocs.size(); i++) {
			String docText = topDocs.get(i).text;
			// 길이 제한: 문서가 너무 길면 요약(또는 앞부분만) 사용하도록 조정하세요
			systemBuilder.append(String.format("[DOC %d]\n%s\n\n", i + 1, docText));
		}
		String systemInstruction = systemBuilder.toString();
		System.out.println("System Instruction:\n" + systemInstruction); // 디버깅용 로그
		// 3) ChatClient 호출 (system + user)
		String response = chatClient.prompt().system(systemInstruction).user(userQuery+" 앞으로의 모든 답변은 반드시 질문한 언어와 동일한 언어로 해줘.").call().content();
		return response;
	}

	// ===== 유틸 메서드와 클래스 =====
	// 검색할 소스인 임베딩 생성
	private double[] embedText(String text) {
		double[] dummy = new double[128];
		int h = Math.min(128, text.length());
		for (int i = 0; i < h; i++)
			dummy[i] = (double) text.charAt(i) / 255.0;
		return dummy;
	}

	// 코사인 유사도 계산 메서드(두 벡터의 유사도를 0~1 사이로 반환 - 1에 가까울수록 유사)
	private double cosineSimilarity(double[] a, double[] b) {
		double dot = 0.0, na = 0.0, nb = 0.0;
		for (int i = 0; i < a.length && i < b.length; i++) {
			dot += a[i] * b[i];
			na += a[i] * a[i];
			nb += b[i] * b[i];
		}
		if (na == 0 || nb == 0)
			return 0.0;
		return dot / (Math.sqrt(na) * Math.sqrt(nb));
	}

	// 메모리기반 문서 벡터 저장용 클래스(아래)
	private static class DocumentVector {
		final String id;
		final String text;
		final double[] embedding;

		DocumentVector(String id, String text, double[] embedding) {
			this.id = id;
			this.text = text;
			this.embedding = embedding;
		}
	}
}
