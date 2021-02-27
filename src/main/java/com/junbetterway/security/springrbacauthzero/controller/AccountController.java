package com.junbetterway.security.springrbacauthzero.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junbetterway.security.springrbacauthzero.model.Account;
import com.junbetterway.security.springrbacauthzero.permission.HasCreateAccountPermission;
import com.junbetterway.security.springrbacauthzero.permission.HasReadAccountPermission;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * @author junbetterway
 *
 */
@RestController
@RequestMapping(path = "api/account")
@CrossOrigin(origins = "*", maxAge = 3600)
@Log4j2
public class AccountController {

    @GetMapping
    @HasReadAccountPermission
    public ResponseEntity<Account> read() {
    	log.info("Accessing read API - needs SCOPE_read:accounts");
    	Account account = Account.builder()
    			.name("Jun King Minon")
    			.balance(new BigDecimal("5500"))
    			.build();
		return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping
    @HasCreateAccountPermission
    public ResponseEntity<Account> create(@RequestBody Account request) {
    	log.info("Accessing create API - needs SCOPE_create:accounts!");
    	Account account = Account.builder()
    			.name(request.getName())
    			.balance(request.getBalance())
    			.build();
		return new ResponseEntity<>(account, HttpStatus.OK);
    }
    
}
