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

<form action="${context}/query/instances" method="GET">

    <div class="row">
        <div class="col-sm-6 form-group">
            <label>Descrizione</label>
			<input name="descrizione" class="form-control" value="${req.descrizione!''}" />
        </div>
		
		<div class="col-sm-3 form-group">
            <label>Processo</label>
			
			<select name="Processo" class="form-control">
				<option value=""></option>
			<#list buffers.processes as p>
				<option value="${p.id}" ${ ((req.Processo!'') == p.id)?string('selected','') }  } >${p.name}</option>
			</#list>
			</select>
        </div>
		
		<div class="col-sm-3 form-group">
            <label>Stato</label>
			<span style="display: inline-block;vertical-align: top;padding: 6px;">
			<input name="Stato" type="checkbox" value="1" ${ ((lreq['Stato']![])?seq_contains("1"))?string('checked','') } }  >Attiva<br/>
			<input name="Stato" type="checkbox" value="2" ${ ((lreq['Stato']![])?seq_contains("2"))?string('checked','') } } >Completa<br/>
			<input name="Stato" type="checkbox" value="3" ${ ((lreq['Stato']![])?seq_contains("3"))?string('checked','') } } >Cancellata<br/>
			</span>
        </div>
	</div>
	
	<div class="row">
		<div class="col-sm-3 form-group">
            <label>Data inizio da</label>
			<input name="data_inizia_da" type="date" value="${req.data_inizia_da!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>Data inizio a</label>
			<input name="data_inizia_a_dp" type="date" value="${req.data_inizia_a_dp!''}" class="form-control">
        </div>
		
		<div class="col-sm-3 form-group">
            <label>Data fine da</label>
			<input name="data_fine_da" type="date" value="${req.data_fine_da!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>Data fine a</label>
			<input name="data_fine_a_dp" type="date" value="${req.data_fine_a_dp!''}" class="form-control">
        </div>
	</div>

    <div class="col-sm-12">
		<br/>
		<button class="btn btn-primary pull-right" type="submit">Esegui&nbsp;</button>
	</div>
</form>