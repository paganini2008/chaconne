<div id="stackTraceBox">
<#list stackTraceArray as trace>
<div class="traceItem">
<pre>${(trace.stackTrace ? html)!}</pre>
</div>
</#list>
</div>