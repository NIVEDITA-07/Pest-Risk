$word = New-Object -ComObject Word.Application
$word.Visible = $false
try {
    $doc = $word.Documents.Open("D:\uhv\PestRisk_UHV_Project_Report_v3.docx", $false, $true)
    $pages = $doc.ComputeStatistics(2)
    Write-Output "EXACT WORD PAGE COUNT: $pages"
    $doc.Close($false)
} catch {
    Write-Output "Error: $_"
} finally {
    $word.Quit()
}
