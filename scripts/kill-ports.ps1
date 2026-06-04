$ErrorActionPreference = 'SilentlyContinue'

$taskkill = Join-Path $env:SystemRoot 'System32\taskkill.exe'
$ports = 8081, 8082, 9090

function Get-ProtectedPids {
    $protected = New-Object 'System.Collections.Generic.HashSet[int]'
    $null = $protected.Add($PID)

    $procId = $PID
    for ($i = 0; $i -lt 10; $i++) {
        $proc = Get-CimInstance Win32_Process -Filter "ProcessId=$procId" -ErrorAction SilentlyContinue
        if (-not $proc -or -not $proc.ParentProcessId) { break }
        $parentId = [int]$proc.ParentProcessId
        if ($parentId -le 0 -or $protected.Contains($parentId)) { break }
        $null = $protected.Add($parentId)
        $procId = $parentId
    }

    return $protected
}

$protectedPids = Get-ProtectedPids

function Stop-PortListener {
    param([int]$Port)

    $connections = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    foreach ($conn in $connections) {
        $pid = [int]$conn.OwningProcess
        if ($pid -le 0 -or $protectedPids.Contains($pid)) { continue }

        Write-Host "  Stopping PID $pid on port $Port"
        if (Test-Path $taskkill) {
            & $taskkill /F /T /PID $pid 2>$null | Out-Null
        } else {
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
        }
    }
}

function Test-PortFree {
    param([int]$Port)
    return -not (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue)
}

foreach ($port in $ports) {
    Stop-PortListener -Port $port
}

Get-CimInstance Win32_Process -Filter "name='java.exe'" |
    Where-Object {
        -not $protectedPids.Contains([int]$_.ProcessId) -and (
            $_.CommandLine -like '*GraduateProjectApplication*' -or
            $_.CommandLine -like '*com.graduate.GraduateProjectApplication*'
        )
    } |
    ForEach-Object {
        Write-Host "  Stopping Graduate Project Java PID $($_.ProcessId)"
        if (Test-Path $taskkill) {
            & $taskkill /F /T /PID $_.ProcessId 2>$null | Out-Null
        } else {
            Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue
        }
    }

foreach ($port in $ports) {
    for ($attempt = 1; $attempt -le 5; $attempt++) {
        if (Test-PortFree -Port $port) { break }
        Write-Host "  Port $port still in use, retry $attempt/5..."
        Stop-PortListener -Port $port
        Start-Sleep -Seconds 1
    }

    if (-not (Test-PortFree -Port $port)) {
        Write-Host "  WARNING: port $port is still in use"
    }
}

exit 0
