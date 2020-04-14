package io.github.natsusai.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网关接口
 *
 * @author liufuhong
 * @since 2019-09-20 16:51
 */

@RestController
@RequestMapping("/gateway")
public class GatewayController {

  @GetMapping("/reload-route-cache")
  public ResponseEntity<String> reloadRouteCache() {
    return ResponseEntity.ok("hi!");
  }
}
