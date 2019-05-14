<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr">
    <head>
        <title>BloomReach Experience 13</title>
        <meta name="robots" content="noindex,nofollow,noarchive"/>
        <style type="text/css">
            html {
                height: 100%;
                width: 100%;
                font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
                font-size: 81.25%;
                line-height: 1.2307692308em;
                -ms-text-size-adjust: 100%;
                -webkit-text-size-adjust: 100%
            }

            body {
                margin: 0;
                background-color: #fff;
                color: #22272f;
                display: flex;
                width: 100%;
                min-height: 100%
            }

            svg:not(:root) {
                overflow: hidden
            }

            a {
                color: #1b95e0;
                text-decoration: none
            }

            a:focus, a:hover {
                color: #39434c;
                text-decoration: underline
            }

            a:focus {
                outline: 5px auto -webkit-focus-ring-color;
                outline-offset: -2px
            }

            .login-plugin {
                align-self: center;
                display: flex;
                height: 100%;
                padding: 40px 120px;
                width: 100%
            }

            .login-wrap {
                display: flex;
                flex-direction: column;
                width: 300px;
                z-index: 2
            }

            .login-panel {
                background-color: #fff;
                border-radius: 6px;
                box-shadow: 0 0 12px rgba(0, 0, 0, .2)
            }

            .login-panel-header {
                background: linear-gradient(to right, #ebeced, #f1f1f1);
                border-radius: 6px 6px 0 0;
                min-height: 120px
            }

            .login-panel-header-logo {
                background: url(logo-br-ent.svg) no-repeat scroll center center;
                background-size: 200px;
                height: 120px
            }

            .post{
                width: 250px;
                margin: auto;
                display: flex;
                flex-direction: column
            }

            .login-extend {
                background-color: #f2f2f2;
                display: flex;
                height: 100%;
                left: 0;
                position: fixed;
                top: 0;
                width: 100%;
                z-index: 1;
            }

            .login-background {
                align-items: center;
                display: flex;
                flex-wrap: wrap;
                height: 100%;
                left: 500px;
                min-width: 700px;
                position: relative;
                width: calc(100vw - 500px)
            }

            .login-background > div {
                background: url(login-background-balloon.svg) no-repeat center center;
                background-size: contain;
                display: flex;
                align-items: center;
                justify-content: center;
                height: calc(100% - 80px);
                max-height: 600px;
                min-height: 400px;
                width: 100%
            }

            .back-button{
                display: flex;
                background-color: #21abf4;
                border: 0;
                border-radius: 0 0 6px 6px;
                height: 70px;
                width: 100%;
            }

            .back-link{
                color: #fff;
                font-size: 1.5em;
                margin: auto;
                transition-duration: .5s;
                transition-property: text-shadow,letter-spacing;
            }

            .back-link:hover{
                color: #fff;
                text-decoration: none;
                letter-spacing: .8px;
                text-shadow: 1px 0 0 currentColor;
            }
        </style>
    </head>
    <body class="hippo-root">
        <div id="id22d95415d2de71b85efe76fccc7fc322" class="login-plugin">
            <div class="login-wrap">
                <div class="login-panel">
                    <div class="login-panel-header">
                        <div class="login-panel-header-logo"></div>
                    </div>
                    <div class="login-panel-center">
                        <div class="post">
                            <div class="post-title"><h2 class="label label-green">You have been successfully logged out</h2></div>
                            <p class="quiet large">You have been logged out duew to request or inactivity.</p>
                        </div>
                        <div class="back-button">
                            <a class="back-link" href="<c:url value="/" />">Back to brXM</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="login-extend">
                <div class="login-background">
                    <div></div>
                </div>
            </div>
        </div>
    </body>
</html>