<!doctype html>
<#import "spring.ftl" as spring />
<html lang="en">

<head>
    <#--    <@boot.importBootstrap />-->
    <#--    <@hf.headForHeaderAndFooter />-->
    <#--    <@form.headForInputForm />-->
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>моай</title>
    <#--    <title><@spring.message 'ad' /></title>-->
</head>
<body>
<#list nodes as node>
    <a href="<@spring.url '/vk/group/'/>${node.id}">
        <img src="${node.photo}" alt="${node.screen_name}">
        <p>${node.name}</p>
        <br>
    </a>
</#list>
</body>
</html>