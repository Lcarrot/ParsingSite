<!doctype html>
<html lang="en">
<#import "spring.ftl" as spring />

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Создать альбом</title>
</head>
<body>
<form method="post">
        <input name="name" type="text" placeholder="Введите имя альбома">
        <input name="description" type="text" placeholder="Введите описание альбома">
    <button type="submit"> Отправить </button>
</form>
</body>
</html>