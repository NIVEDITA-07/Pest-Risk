$word = New-Object -ComObject Word.Application
$word.Visible = $false
try {
    $doc = $word.Documents.Open("D:\uhv\PestRisk_UHV_Project_Report.docx", $false, $true)
    foreach ($p in $doc.Paragraphs) {
        $text = $p.Range.Text.Trim()
        if ($text -match "^(CHAPTER|[0-9]\.[0-9]|DECLARATION|CERTIFICATE|ACKNOWLEDGEMENT|INSTITUTE|DEPARTMENT|PROGRAMME|Abstract|TABLE OF CONTENTS|LIST OF|REFERENCE|Fig\.)") {
            $pageNum = $p.Range.Information(3)
            Write-Output "Page $pageNum : $text"
        }
    }
    $doc.Close($false)
} catch {
    Write-Output "Error: $_"
} finally {
    $word.Quit()
}
