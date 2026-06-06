$src = 'D:\hust-kindergarden\assets\Final Outside\Final Outside.tmx'
$dst = 'D:\hust-kindergarden\assets\Final Outside.tmx'
$content = [System.IO.File]::ReadAllText($src, [System.Text.Encoding]::UTF8)
$old = 'source="outside.tsx"'
$new = 'source="Final Outside/outside.tsx"'
$content = $content.Replace($old, $new)
[System.IO.File]::WriteAllText($dst, $content, [System.Text.Encoding]::UTF8)
Write-Host "Done - Map replaced successfully"
