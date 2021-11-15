<style>

.token-input-input-token-facebook input{
box-shadow: none !important;
cursor: text  !important;
height: 22px !important;
}

.token-input-token-facebook span {
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
max-width: 130px;
display: inline-block;
vertical-align: middle;
}

.ftl-view span{
display:none
}

.ftl-tasks-form::after {
content : "Ricerca con auto-completion"
}

.ftl-tasks-form-combo::after {
content : "Ricerca con combo-box"
}


</style>

<form action="${context}/query/tasks" method="GET">

<div class="row">
<div class="col-sm-3 form-group">
<label>In carico</label>
<input name="Incaricato" value="${req.Incaricato!''}" data-tokenLimit="3" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=type:user" >
</div>

<div class="col-sm-3 form-group">
<label>Interessato</label>
<input name="Interessato" value="${req.Interessato!''}" data-tokenLimit="3" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=type:user" >
</div>

<div class="col-sm-6 form-group">
<label>Ruolo</label>
<input name="Ruolo" value="${ (lreq.Ruolo![])?join(',') }" data-tokenLimit="3" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=type:group" >
</div>
</div>

<div class="row">

<div class="col-sm-3 form-group">
<label>Processo</label>
<!--<input name="Processo" class="auto-completion form-control" data-source="${context}/query/auto-processes?" >-->

<select name="Processo" class="form-control">
<option value=""></option>
<#list buffers.processes as p>
<option value="${p.id}" ${ ((req.Processo!'') == p.id)?string('selected','') }  } >${p.name}</option>
</#list>
</select>
</div>

<div class="col-sm-3 form-group">
<label>Scadenza</label>
<!-- ${ utils.datetime(req.Scadenza_dp) } -->
<input name="Scadenza_dp" type="date" value="${ utils.isodate(req.Scadenza_dp!'') }" class="form-control">
</div>

<div class="col-sm-3 form-group">
<label>Allegato</label>
<input name="Allegato" type="checkbox" ${ ( (req.Allegato!'')=="1")?string('checked','') } value="1" >
</div>

<div class="col-sm-3 form-group">
<label>Stato</label>
<span style="display: inline-block;vertical-align: top;padding: 6px;">
<input name="Stato" type="checkbox" ${ ((lreq['Stato']![])?seq_contains("Created"))?string('checked','') } } value="Created" >Creato<br/>
<input name="Stato" type="checkbox" ${ ((lreq['Stato']![])?seq_contains("Ready"))?string('checked','') } } value="Ready" >In entrata<br/>
<input name="Stato" type="checkbox" ${ ((lreq['Stato']![])?seq_contains("InProgress"))?string('checked','') } } value="InProgress" >In carico<br/>
<input name="Stato" type="checkbox" ${ ((lreq['Stato']![])?seq_contains("Completed"))?string('checked','') } } value="Completed" >Completato<br/>
<input name="Stato" type="checkbox" ${ ((lreq['Stato']![])?seq_contains("Exited"))?string('checked','') } } value="Exited" >Annullato
</span>
</div>
</div>

<div class="row">
<button class="btn btn-primary pull-right" type="submit">Esegui&nbsp;</button>
</div>
</form>