package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;

import java.util.List;
import java.util.Optional;

public class MemberService {
    private final MemberRepository memberRepository;

    // 생성자 생성 단축키 : alt + insert
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
    * 회원가입
    * 1. 중복 회원 검증(같은 이름이 있는 중복 회원 X)
    * 2. 회원가입(저장)
    * */
    public Long join(Member member){
        /* 1. 중복 회원 검증(같은 이름이 있는 중복 회원 X) */

//            //------------- 방법[1] : result로 반환받아서 사용하기
//            // optional 씌워주는 단축키 : command + option + V / ctrl + alt + v
//            Optional<Member> result = memberRepository.findByName(member.getName());
//            result.ifPresent(m -> { // ifPresent : 값이 있으면, throw를 던진다.
//                throw new IllegalStateException("이미 존재하는 회원입니다.");
//            }); // 이 함수는 optional로 감싸져 있어서 가능하다.
//
//            //------------- 방법[2] : 어짜피 반환 되었으니, 바로 사용하기
//            memberRepository.findByName(member.getName()) // 이거의 결과는 optional이니까
//                            .ifPresent(m -> {
//                                throw new IllegalStateException("이미 존재하는 회원입니다.");
//                            });

            //------------- 방법[3] : 로직이 있는 경우, 함수로 뽑아내기
            // 함수로 뽑아내는 단축키 : command + option + m / ctrl + alt + m
            validateDuplicateMember(member);

        /* 2. 회원가입(저장) */
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName()) // 이거의 결과는 optional이니까
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
    * 전체 회원 조회
    * */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 조회
     * */
    public Optional<Member> findOne(Long memeberId) {
        return memberRepository.findById(memeberId);
    }
}













//package hello.hellospring.service;
//
//import hello.hellospring.domain.Member;
//import hello.hellospring.repository.MemberRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public class MemberService {
//
//    private final MemberRepository memberRepository;
//
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
//
//    // 회원가입
//    public Long join(Member member){
//        // 중복되는 이름 X
//        // optional 씌워주는 단축키: Ctrl + Alt + V
//
//        /*
//            Optional<Member> result = memberRepository.findByName(member.getName());
//            result.ifPresent(m ->{ // ifPresent..?
//                throw new IllegalStateException("이미 존재하는 회원입니다.");
//            });
//        */
//
//        // 위의 코드와 아래의 코드는 똑같다! 보기 좋고 편하게 쓰자!
//
//        // ctrl + alt + M 하면 메소드 추출 단축키!
//        validateDuplicateMember(member); // 중복검사
//        memberRepository.save(member); // 통과하면 저장한다
//        return member.getId();
//    }
//
//    private void validateDuplicateMember(Member member) {
//        memberRepository.findByName(member.getName()).ifPresent(m ->{
//            throw new IllegalStateException("이미 존재하는 회원입니다.");
//        });
//    }
//
//    // 전체 회원 조회
//    public List<Member> findMembers(){
//        return memberRepository.findAll();
//    }
//
//    // 회원 정보 한개 가져오기
//    public Optional<Member> findeOne(Long memberId){
//        return memberRepository.findById(memberId);
//    }
//}
