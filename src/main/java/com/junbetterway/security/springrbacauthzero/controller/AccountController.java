package com.junbetterway.security.springrbacauthzero.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junbetterway.security.springrbacauthzero.model.Account;
import com.junbetterway.security.springrbacauthzero.permission.HasReadAccountPermission;
import com.junbetterway.security.springrbacauthzero.permission.HasReadSystemAccountPermission;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author junbetterway
 *
 */
@RestController
@RequestMapping(path = "api/account")
@Slf4j
public class AccountController {

    @GetMapping("guest")
    @HasReadAccountPermission
    public ResponseEntity<Account> readGuestAccount() {
    	log.info("Accessing readGuestAccount API - needs SCOPE_read:accounts");
    	Account account = Account.builder()
    			.name("Jun King Minon")
    			.balance(new BigDecimal("5500"))
    			.build();
		return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("system")
    @HasReadSystemAccountPermission
    public ResponseEntity<Account> readAdminAccount() {
    	log.info("Accessing readAdminAccount API - needs SCOPE_read:system:accounts!");
        Account account = Account.builder()
                .name("Better Way System")
                .balance(new BigDecimal("25000000"))
                .build();
		return new ResponseEntity<>(account, HttpStatus.OK);
    }
    
}
