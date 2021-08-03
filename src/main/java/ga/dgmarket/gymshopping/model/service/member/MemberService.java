package ga.dgmarket.gymshopping.model.service.member;

import java.util.List;

import ga.dgmarket.gymshopping.domain.Member;

public interface MemberService {
	public Member login(Member member);//로그인
	public void regist(Member member);//회원가입
	public void update(Member member);//정보 수정
	public void update2(Member member); //탈퇴시 user_grade를 8로 변경하여 눈속임
	//public void delete(int member_id);//아이디, 패스워드가 모두 일치할 때 탈퇴
	public List selectAll();
	public Member select(int member_id);
	public int idCheck(String memberId);
	public int countUser();
	public void updateByAdmin(Member member); // 관리자의 회원수정
}
