package ga.dgmarket.gymshopping.controller.member;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ga.dgmarket.gymshopping.domain.Member;
import ga.dgmarket.gymshopping.exception.DMLException;
import ga.dgmarket.gymshopping.exception.FileHandleException;
import ga.dgmarket.gymshopping.exception.MemberExistException;
import ga.dgmarket.gymshopping.exception.UploadException;
import ga.dgmarket.gymshopping.model.common.file.FileManager;
import ga.dgmarket.gymshopping.model.email.DM;
import ga.dgmarket.gymshopping.model.email.EmailSender;
import ga.dgmarket.gymshopping.model.service.member.MemberService;
import ga.dgmarket.gymshopping.model.service.product.ProductService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class MemberController {
	

	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	@Autowired
	private MemberService memberService;

	@Autowired
	private FileManager fileManager;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private EmailSender emailSender;
	

	// 로그인 폼 요청 처리
	@RequestMapping(value = "/loginform", method = RequestMethod.GET)
	public String loginForm() {

		return "member/login/loginform";
	}

	

	// 로그인 요청 처리
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(Member member, HttpSession session, Model model) {
		// 3단계: 일 시키기
		Member obj = memberService.login(member);

		// 4단계: 저장
		session.setAttribute("member", obj);
		model.addAttribute("member", obj);
		return "member/main/index";
	}
	
	//멤버 메인 페이지 요청 및 리퀘스트에 상품저장 --도균--
		@GetMapping("/main")
		public String getMain(HttpServletRequest request) {
			
			// 3단계: 일 시키기
			List productNewList = productService.selectNewItem();
			
			// 4단계: 저장
			request.setAttribute("productNewList", productNewList);
			
			return "member/main/main";
		}

	// 회원가입 폼 요청
	@GetMapping("/registform")
	public String joinForm(Model model) {
		return "member/login/join/regist";
	}
	
	// 아이디 중복 검사
		@RequestMapping(value = "/memberIdChk", method = RequestMethod.POST)
		@ResponseBody
		public String memberIdChkPOST(Member member) throws Exception{
			int result = memberService.idCheck(member.getUser_id());
			
			if(result != 0) {
				return "fail";	// 중복 아이디가 존재
			} else {
				return "success";	// 중복 아이디 x		
			}	
		}

	// 회원가입 요청
	@PostMapping("/main/regist")
	public String join(Member member, HttpServletRequest request) {

		// VO에 등록한 MultipartFile 객체에 업로드된 파일이 이미 들어있으므로 개발자는 이 객체를 이용하여 업로드된 파일을 원하는대로
		// 제어하면 된다
		MultipartFile photo = member.getPhoto();
		ServletContext context = request.getServletContext();
		long time = System.currentTimeMillis();// 현재 날짜 구하기

		// 원하는 위치에 파일 저장하기
		String filename = time + "." + fileManager.getExt(photo.getOriginalFilename());
		fileManager.saveFile(context, filename, photo);
		System.out.println(filename);
		member.setProfile_img(filename);
		memberService.regist(member);
		HttpSession session = request.getSession();
		session.setAttribute("member", member);
		
		return "member/login/loginform";
	}

	// 회원정보상세 요청
	@GetMapping("/join/detail")
	public String getDetail(int member_id, Model model) {
		Member member = memberService.select(member_id);
		List memberList = memberService.selectAll();

		model.addAttribute("member", member);
		model.addAttribute("memberList", memberList);

		return "member/login/join/content";
	}
	
	//회원수청요청처리
	@PostMapping("/join/update")
	public String update(Member member, HttpServletRequest request, Model model) {
		System.out.println("지금 멤버 프로필 이미지는 "+member.getProfile_img());
		System.out.println("member's photoe "+member.getPhoto().getOriginalFilename().length());
		int leng=member.getPhoto().getOriginalFilename().length();
		
		if(leng>0) {
			ServletContext context = request.getServletContext();
			fileManager.deleteFile(context, member.getProfile_img());

			long time = System.currentTimeMillis();// 현재 날짜 구하기

			// 원하는 위치에 파일 저장하기
			String filename = time + "." + fileManager.getExt(member.getPhoto().getOriginalFilename());
			fileManager.saveFile(context, filename, member.getPhoto());
			member.setProfile_img(filename);
			
		}
		memberService.update(member);

		return "member/main/index";
	}

	// 회원탈퇴요청처리
	@PostMapping("/join/del")
	public String delete(Member member, HttpServletRequest request) {
		memberService.delete(member.getMember_id());
		fileManager.deleteFile(request.getServletContext(), member.getProfile_img());

		return "member/main/index";
	}
	
	//이메일
	@RequestMapping("/certifiedMail")
	@ResponseBody
	public String certifiedMail(@RequestParam(required = false)String user_email) {
		System.out.println("들어왔니?");
		logger.info("해당 유저의 이메일 확인",user_email);
		
		String email="";
		String subject="";
		String content="";
		String receiver="";
		String sender="";
		
		int authCode=0;
		String authCodes="";
		boolean bool=false;
		
		if(user_email!=null && user_email.isEmpty()) {
			email=user_email;
			logger.info("이메일 계정체크={}",email);
			for(int i=0;i<6;i++) {
				authCode=(int)(Math.random()*9+1);
				authCodes+=Integer.toString(authCode);
				logger.info("6자리 랜덤={}",authCode);
			}
			
			logger.info("난수 체크={}", authCodes);
			
			subject="안녕하세요 둑근마켓 페이지의 관리자입니다. 화원가입 인증번호입니다.";
			content=DM.dmCertification(authCodes);
			receiver=email;
			sender="vndn1679@gmail.com";
		}
		
		try {
			emailSender.sendMail(subject, content, receiver, sender);
			logger.info("이메일 발송성공");
		} catch (MessagingException e) {
			logger.info("이메일 발송 실패");
			e.printStackTrace();
		}
		
		return authCodes;
	}

	// 위의 요청을 처리하는 메서드 중에서 어느것 하나라도 예외가 발생하면 아래의 메서드가 동작하게 됨
	@ExceptionHandler(MemberExistException.class)
	public String handleException(MemberExistException e, Model model) {
		model.addAttribute("result", "false");// 에러객체 저장

		return "member/login/loginform";
	}

	@ExceptionHandler(UploadException.class)
	public String handleException(UploadException e, Model model) {
		model.addAttribute("e", e); // 에러객체 담기

		return "error/result";
	}

	@ExceptionHandler(FileHandleException.class)
	public String handleException(FileHandleException e, Model model) {
		model.addAttribute("e", e); // 에러객체 담기
		return "error/result";
	}

}
