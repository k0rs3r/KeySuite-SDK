<h4>Ricerca avanzata fascicoli</h4>

<input name="type" type="hidden" value="fascicolo" />

<#include "common-search.ftl" />

<div class="panel panel-default" >

<div class="panel-heading" >
	<span class="panel-title">Dati del fascicolo</span>
</div>

<div class="panel-body" >

    <div class="row">
	
		<div class="col-sm-3 form-group">
            <label>Anno</label>
			<input name="ANNO_FASCICOLO" class="auto-completion form-control" value="${req.ANNO_FASCICOLO!''}" data-source="${context}/query/solr-auto-facet?field=ANNO_FASCICOLO" />
        </div>
	
		<div class="col-sm-3 form-group">
			<label>Classifica</label>
			<input name="CLASSIFICA" value="${req.CLASSIFICA!''}" data-tokenLimit="1" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=+type:titolario" >
		</div>
		
		<div class="col-sm-3 form-group">
            <label># Progressivo</label>
			<input name="PROGR_FASCICOLO" class="form-control" value="${req.PROGR_FASCICOLO!''}" />
        </div>
		
		<div  class="col-sm-3 form-group checkbox">
			<label>
				<input name="parent" type="checkbox" ${ ( (req.parent!'')=="*@titolario")?string('checked','') } value="*@titolario" >
				Solo primo livello
			</label><br/>
			
			<label>
				<input name="parent" type="checkbox" ${ ( (req.parent!'')=="*@fascicolo")?string('checked','') } value="*@fascicolo" >
				Sotto fascicoli
			</label>
		</div>
		
		
	</div>
	<div class="row">
		
		<div class="col-sm-3 form-group">
            <label>Data chiusura da</label>
			<input name="DATA_CHIUSURA[" type="date" value="${req['DATA_CHIUSURA[']!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>a</label>
			<input name="DATA_CHIUSURA]" type="date" value="${req['DATA_CHIUSURA]']!''}" class="form-control">
        </div>
		
		
		
		
		<div style="margin-top: -5px;" class=" col-sm-3 form-group checkbox">
			
			<label>
				<input name="acl_inherits" type="checkbox" ${ ( (req.acl_inherits!'')=="true")?string('checked','') } value="true" >
				Riservato
			</label><br/>
			
			<label>
				<input name="acl_inherits" type="checkbox" ${ ( (req.acl_inherits!'')=="false")?string('checked','') } value="false" >
				Pubblico
			</label>
		</div>
		
		
		<div class=" col-sm-3 form-group checkbox">
			
			<label>
				<input name="ENABLED" type="checkbox" ${ ( (req.ENABLED!'')=="true")?string('checked','') } value="true" >
				Aperto
			</label><br/>
			
			<label>
				<input name="ENABLED" type="checkbox" ${ ( (req.ENABLED!'')=="false")?string('checked','') } value="false" >
				Chiuso
			</label>
		</div>	

				

	</div>
	
	<div class="row" style="display:${ (buffers.tipologie_fasc?size<2)?string('none','block') }" >
	
		<div class="col-sm-3 form-group">
            <label>Tipologia</label>
			
			<select  name="TYPE_ID" onchange="location.href='?${utils.remove(querystringParams,'TYPE_ID')}&TYPE_ID='+$(this).val()" class="form-control">
				<option value=""></option>
			<#list buffers.tipologie_fasc as tip >
				<option value="${tip.value}" ${ ((req.TYPE_ID!'') == tip.value)?string('selected','') }  } >$[facet.TYPE_ID.${tip.value}:${tip.value?lower_case?cap_first?replace('_',' ')}] (${tip.count})</option>
			</#list>
			</select>
        </div>
		
	</div>
	
	<#include  "tipologie/"+(req.TYPE_ID!"NOT_FOUND")+".ftl" ignore_missing=true  />
	
</div>
	
</div>	
	

