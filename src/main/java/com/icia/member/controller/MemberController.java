package com.icia.member.controller;


import com.icia.member.dto.MemberDTO;
import com.icia.member.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/memberPages")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/memberSave")
    public String memberSave(){
        return "memberPages/memberSave";
    }

    @PostMapping("/memberSave")
    public String memberSave(@ModelAttribute MemberDTO memberDTO){
        memberService.save(memberDTO);
        return "redirect:/memberPages/memberList";
    }

    @GetMapping("/memberList")
    public String findAll(Model model){
        List<MemberDTO> memberDTOList = memberService.findAll();
        model.addAttribute("memberList", memberDTOList);
        return "memberPages/memberList";
    }

    @GetMapping("/memberLogin")
    public String memberLogin(@RequestParam(value = "redirectURI", defaultValue = "/member/mypage") String redirectURI, Model model) {
        model.addAttribute("redirectURI", redirectURI);
        return "memberPages/memberLogin";
    }

    @PostMapping("/memberLogin")
    public String memberLogin(@ModelAttribute MemberDTO memberDTO, HttpSession session,
                              @RequestParam("redirectURI") String redirectURI){
        boolean loginResult = memberService.login(memberDTO);
        if (loginResult) {
            session.setAttribute("loginEmail", memberDTO.getMemberEmail());
//            return "memberPages/memberMain";
            // 사용자가 로그인 성공하면, 직전에 요청한 페이지로 이동시킴.
            // 별도로 요청한 페이지가 없다면 정상적으로 myPage로 이동시킴.(redirect:/member/mypage)
            return "redirect:" + redirectURI;
        }else {
            return "memberPages/memberLogin";
        }
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
        try {
            MemberDTO memberDTO = memberService.findById(id);
            model.addAttribute("member", memberDTO);
            return "memberPages/memberDetail";
        } catch (NoSuchElementException e) {
            return "memberPages/NotFound";
        } catch (Exception e) {
            return "memberPages/NotFound";
        }
    }

    @GetMapping("/mypage")
    public String myPage(){
        return "memberPages/memberMain";
    }

    @PostMapping("/dup-check")
    public ResponseEntity emailCheck(@RequestBody MemberDTO memberDTO) {
        boolean result = memberService.emailCheck(memberDTO.getMemberEmail());
        if (result) {
            return new ResponseEntity<>("사용가능", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("사용불가능", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/axios/{id}")
    public ResponseEntity detailAxios(@PathVariable("id") Long id) {
        try {
            MemberDTO memberDTO = memberService.findById(id);
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody MemberDTO memberDTO, HttpSession session) {
//        memberService.update(memberDTO);
        session.removeAttribute("loginEmail");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        memberService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
