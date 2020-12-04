(function (_, Kotlin, $module$react_router_dom, $module$kotlin_react) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var getKClass = Kotlin.getKClass;
  var HashRouterComponent = $module$react_router_dom.HashRouter;
  var BrowserRouterComponent = $module$react_router_dom.BrowserRouter;
  var SwitchComponent = $module$react_router_dom.Switch;
  var get_js = Kotlin.kotlin.js.get_js_1yb8b7$;
  var Unit = Kotlin.kotlin.Unit;
  var RouteComponent = $module$react_router_dom.Route;
  var LinkComponent = $module$react_router_dom.Link;
  var NavLinkComponent = $module$react_router_dom.NavLink;
  var RedirectComponent = $module$react_router_dom.Redirect;
  function hashRouter($receiver, handler) {
    return $receiver.child_bzgiuu$(getKClass(HashRouterComponent), handler);
  }
  function browserRouter($receiver, handler) {
    return $receiver.child_bzgiuu$(getKClass(BrowserRouterComponent), handler);
  }
  function switch_0($receiver, handler) {
    return $receiver.child_bzgiuu$(getKClass(SwitchComponent), handler);
  }
  function route$lambda$lambda(closure$path, closure$exact, closure$strict, closure$component) {
    return function ($receiver) {
      $receiver.path = closure$path;
      $receiver.exact = closure$exact;
      $receiver.strict = closure$strict;
      $receiver.component = get_js(closure$component);
      return Unit;
    };
  }
  function route$lambda(closure$path, closure$exact, closure$strict, closure$component) {
    return function ($receiver) {
      $receiver.attrs_slhiwc$(route$lambda$lambda(closure$path, closure$exact, closure$strict, closure$component));
      return Unit;
    };
  }
  function route($receiver, path, component, exact, strict) {
    if (exact === void 0)
      exact = false;
    if (strict === void 0)
      strict = false;
    return $receiver.child_bzgiuu$(getKClass(RouteComponent), route$lambda(path, exact, strict, component));
  }
  function route$lambda$lambda_0(closure$path, closure$exact, closure$strict, closure$render) {
    return function ($receiver) {
      $receiver.path = closure$path;
      $receiver.exact = closure$exact;
      $receiver.strict = closure$strict;
      $receiver.render = closure$render;
      return Unit;
    };
  }
  function route$lambda_0(closure$path, closure$exact, closure$strict, closure$render) {
    return function ($receiver) {
      $receiver.attrs_slhiwc$(route$lambda$lambda_0(closure$path, closure$exact, closure$strict, closure$render));
      return Unit;
    };
  }
  function route_0($receiver, path, exact, strict, render) {
    if (exact === void 0)
      exact = false;
    if (strict === void 0)
      strict = false;
    return $receiver.child_bzgiuu$(getKClass(RouteComponent), route$lambda_0(path, exact, strict, render));
  }
  function route$lambda$lambda$lambda(closure$render) {
    return function (it) {
      return closure$render();
    };
  }
  function route$lambda$lambda_1(closure$path, closure$exact, closure$strict, closure$render) {
    return function ($receiver) {
      $receiver.path = closure$path;
      $receiver.exact = closure$exact;
      $receiver.strict = closure$strict;
      $receiver.render = route$lambda$lambda$lambda(closure$render);
      return Unit;
    };
  }
  function route$lambda_1(closure$path, closure$exact, closure$strict, closure$render) {
    return function ($receiver) {
      $receiver.attrs_slhiwc$(route$lambda$lambda_1(closure$path, closure$exact, closure$strict, closure$render));
      return Unit;
    };
  }
  function route_1($receiver, path, exact, strict, render) {
    if (exact === void 0)
      exact = false;
    if (strict === void 0)
      strict = false;
    return $receiver.child_bzgiuu$(getKClass(RouteComponent), route$lambda_1(path, exact, strict, render));
  }
  function routeLink$lambda$lambda(closure$to, closure$replace, closure$className) {
    return function ($receiver) {
      $receiver.to = closure$to;
      $receiver.replace = closure$replace;
      $receiver.className = closure$className;
      return Unit;
    };
  }
  function routeLink$lambda(closure$to, closure$replace, closure$className, closure$handler) {
    return function ($receiver) {
      $receiver.attrs_slhiwc$(routeLink$lambda$lambda(closure$to, closure$replace, closure$className));
      closure$handler != null ? closure$handler($receiver) : null;
      return Unit;
    };
  }
  function routeLink($receiver, to, replace, className, handler) {
    if (replace === void 0)
      replace = false;
    if (className === void 0)
      className = null;
    return $receiver.child_bzgiuu$(getKClass(LinkComponent), routeLink$lambda(to, replace, className, handler));
  }
  function navLink$lambda$lambda(closure$to, closure$replace, closure$className, closure$activeClassName, closure$exact, closure$strict, closure$isActive) {
    return function ($receiver) {
      $receiver.to = closure$to;
      $receiver.replace = closure$replace;
      $receiver.className = closure$className;
      $receiver.activeClassName = closure$activeClassName;
      $receiver.exact = closure$exact;
      $receiver.strict = closure$strict;
      $receiver.isActive = closure$isActive;
      return Unit;
    };
  }
  function navLink$lambda(closure$to, closure$replace, closure$className, closure$activeClassName, closure$exact, closure$strict, closure$isActive, closure$handler) {
    return function ($receiver) {
      $receiver.attrs_slhiwc$(navLink$lambda$lambda(closure$to, closure$replace, closure$className, closure$activeClassName, closure$exact, closure$strict, closure$isActive));
      closure$handler != null ? closure$handler($receiver) : null;
      return Unit;
    };
  }
  function navLink($receiver, to, replace, className, activeClassName, exact, strict, isActive, handler) {
    if (replace === void 0)
      replace = false;
    if (className === void 0)
      className = null;
    if (activeClassName === void 0)
      activeClassName = 'active';
    if (exact === void 0)
      exact = false;
    if (strict === void 0)
      strict = false;
    if (isActive === void 0)
      isActive = null;
    return $receiver.child_bzgiuu$(getKClass(NavLinkComponent), navLink$lambda(to, replace, className, activeClassName, exact, strict, isActive, handler));
  }
  function redirect$lambda$lambda(closure$from, closure$to, closure$push, closure$exact, closure$strict) {
    return function ($receiver) {
      $receiver.from = closure$from;
      $receiver.to = closure$to;
      $receiver.push = closure$push;
      $receiver.exact = closure$exact;
      $receiver.strict = closure$strict;
      return Unit;
    };
  }
  function redirect$lambda(closure$from, closure$to, closure$push, closure$exact, closure$strict) {
    return function ($receiver) {
      $receiver.attrs_slhiwc$(redirect$lambda$lambda(closure$from, closure$to, closure$push, closure$exact, closure$strict));
      return Unit;
    };
  }
  function redirect($receiver, from, to, push, exact, strict) {
    if (push === void 0)
      push = false;
    if (exact === void 0)
      exact = false;
    if (strict === void 0)
      strict = false;
    return $receiver.child_bzgiuu$(getKClass(RedirectComponent), redirect$lambda(from, to, push, exact, strict));
  }
  var package$react = _.react || (_.react = {});
  var package$router = package$react.router || (package$react.router = {});
  var package$dom = package$router.dom || (package$router.dom = {});
  package$dom.hashRouter_jg12zk$ = hashRouter;
  package$dom.browserRouter_jg12zk$ = browserRouter;
  package$dom.switch_jg12zk$ = switch_0;
  $$importsForInline$$['kotlin-react'] = $module$kotlin_react;
  package$dom.route_tvrvhh$ = route;
  package$dom.route_oxctnt$ = route_0;
  package$dom.route_9tkfd6$ = route_1;
  package$dom.routeLink_4zdlmx$ = routeLink;
  package$dom.navLink_bcialx$ = navLink;
  package$dom.redirect_l8ye1a$ = redirect;
  Kotlin.defineModule('kotlin-react-router-dom', _);
  return _;
}(module.exports, require('kotlin'), require('react-router-dom'), require('kotlin-react')));

//# sourceMappingURL=kotlin-react-router-dom.js.map
