
# 📘 Guia de Comandos Git — Básico ao Avançado

Este documento traz os principais comandos do Git, organizados por nível e com explicações diretas sobre suas funcionalidades.

---

## 🟢 INICIANDO COM GIT

### 🔹 Configurar usuário (apenas uma vez por máquina)

```bash
git config --global user.name "Seu Nome"
git config --global user.email "seuemail@exemplo.com"
```

---

### 🔹 Inicializar um repositório Git

```bash
git init
```

Cria um novo repositório Git na pasta atual.

---

### 🔹 Clonar repositório remoto

```bash
git clone https://github.com/usuario/repositorio.git
```

---

### 🔹 Ver o status do repositório

```bash
git status
```

Mostra alterações pendentes, arquivos não versionados e mudanças prontas para commit.

---

## 🟡 TRABALHANDO COM COMMIT

### 🔹 Adicionar arquivos ao staging (preparar para commit)

```bash
git add nome_arquivo
git add .               # Adiciona todos os arquivos modificados
```

---

### 🔹 Criar um commit

```bash
git commit -m "mensagem do commit"
```

---

### 🔹 Ver histórico de commits

```bash
git log
```

Use `q` para sair da visualização.

---

## 🔵 BRANCHES (RAMIFICAÇÕES)

### 🔹 Criar nova branch

```bash
git branch nome-da-branch
```

---

### 🔹 Mudar para outra branch

```bash
git checkout nome-da-branch
```

---

### 🔹 Criar e já mudar para a branch

```bash
git checkout -b nome-da-branch
```

---

### 🔹 Listar branches locais

```bash
git branch
```

---

### 🔹 Listar branches remotas

```bash
git branch -r
```

---

### 🔹 Listar todas as branches (locais e remotas)

```bash
git branch -a
```

---

### 🔹 Deletar branch local

```bash
git branch -d nome-da-branch     # Só se a branch já foi mesclada
git branch -D nome-da-branch     # Força a exclusão
```

---

### 🔹 Deletar branch remota

```bash
git push origin --delete nome-da-branch
```

---

## 🟣 TRABALHANDO COM REMOTO

### 🔹 Ver repositórios remotos

```bash
git remote -v
```

---

### 🔹 Enviar branch local para o remoto

```bash
git push origin nome-da-branch
```

---

### 🔹 Atualizar seu repositório local com alterações do remoto

```bash
git pull
```

---

## 🔴 COMANDOS AVANÇADOS

### 🔹 Mudar a branch padrão (usando GitHub CLI)

```bash
gh repo edit --default-branch main
```

---

### 🔹 Ver branch padrão atual (via GitHub CLI)

```bash
gh repo view --json defaultBranchRef --jq .defaultBranchRef.name
```

---

### 🔹 Ver detalhes do repositório (via GitHub CLI)

```bash
gh repo view --web       # Abre no navegador
gh repo view --json name,description,defaultBranchRef
```

---

## ✅ DICAS EXTRAS

- Use `git stash` para guardar alterações temporárias.
- Use `git diff` para ver as diferenças entre versões.
- Use `git restore` para desfazer alterações em arquivos.
- Use `git reset` para desfazer commits locais.

---

> 🔁 Sempre que estiver em dúvida, use `git status` para saber o que está acontecendo no seu repositório.
