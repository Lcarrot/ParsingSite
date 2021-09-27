<!doctype html>
<html lang="en">
<#import "spring.ftl" as spring />

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Альбом</title>
</head>
<body>
<a href="<@spring.url '/vk/group/${group_id}/album/create'/>"><button>Создать новый альбом</button></a>
<a href="<@spring.url '/vk/group/${group_id}/album/albums'/>"><button>Выбрать существующий альбом</button></a>
</body>
</html>