<?php
if(!isset($_SESSION)) {
    session_start();
}

if(!isset($_SESSION['id'])) {
    // Exibe uma página de estilo ao invés de apenas texto
    echo '
    <!DOCTYPE html>
    <html lang="pt-br">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Acesso Negado</title>
        <style>
            body {
                margin: 0;
                padding: 0;
                font-family: Arial, sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                background: rgb(160,197,255);
                background: linear-gradient(90deg, rgba(160,197,255,1) 0%, rgba(0,212,255,1) 100%);
            }
            .container {
                text-align: center;
                background-color: white;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                max-width: 400px;
                width: 100%;
            }
            h1 {
                color: #e74c3c;
                font-size: 24px;
                margin-bottom: 20px;
            }
            p {
                font-size: 16px;
                margin-bottom: 30px;
                color: #555;
            }
            a {
                display: inline-block;
                text-decoration: none;
                background-color: darkblue;
                color: white;
                padding: 10px 20px;
                border-radius: 5px;
                font-size: 16px;
                transition: background-color 0.3s ease;
            }
            a:hover {
                background-color: #2980b9;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Acesso Negado</h1>
            <p>Você não pode acessar esta página porque não está logado.</p>
            <a href="login.php">Entrar</a>
        </div>
    </body>
    </html>
    ';
    exit;
}
?>
