<div align=center>
<h1>MikunPic</h1>
</div>

# Deploy

- for user

``` bash
    sudo loginctl enable-linger $USER
    curl -fsSL 'https://raw.githubusercontent.com/mikun12138/MikunPic/refs/heads/main/server/deployment/install-user.sh' | bash
```

- for root (Not tested)
``` bash
    curl -fsSL 'https://raw.githubusercontent.com/mikun12138/MikunPic/refs/heads/main/server/deployment/install.sh' | sudo bash
```
