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
        <div class="col-sm-6 form-group">
            <label>In carico</label>
            <input name="Incaricato" data-tokenLimit="3" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=type:user" >
        </div>
        <div class="col-sm-6 form-group">
            <label>Ruolo</label>
            <input name="Ruolo" data-tokenLimit="3" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=type:group" >
        </div>
    </div>

    <div class="row">

        <div class="col-sm-3 form-group">
            <label>Processo</label>
            <input name="Processo" class="auto-completion form-control" data-source="${context}/query/auto-processes?" >

            <!--<select name="Processo" class="form-control">
				<option value=""></option>
			<#list buffers.processes as p>
				<option value="${p.id}">${p.name}</option>
			</#list>
			</select>-->
        </div>

        <div class="col-sm-3 form-group">
            <label>Scadenza</label>
            <input name="Scadenza_dp" type="date" class="form-control">
        </div>

        <div class="col-sm-3 form-group">
            <label>Allegato</label>
            <input name="Allegato" type="checkbox" value="1" >
        </div>

        <div class="col-sm-3 form-group">
            <label>Stato</label>
            <span style="display: inline-block;vertical-align: top;padding: 6px;">
			<input name="Stato" type="checkbox" value="Created" >Creato<br/>
			<input name="Stato" type="checkbox" value="Ready" >In entrata<br/>
			<input name="Stato" type="checkbox" value="InProgress" >In carico<br/>
			<input name="Stato" type="checkbox" value="Completed" >Completato<br/>
			<input name="Stato" type="checkbox" value="Exited" >Annullato
			</span>
        </div>
    </div>

    <div class="row">
        <button class="btn btn-primary pull-right" type="submit">Esegui&nbsp;</button>
    </div>
</form>