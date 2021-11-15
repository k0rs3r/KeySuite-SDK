<h4>Ricerca avanzata documenti</h4>

<input type="hidden" name="type" value="documento" />

<#include "common-search.ftl" />

<div class="panel panel-default" >

<div class="panel-heading" >
	<span class="panel-title">Dati del documento</span>
</div>

<div class="panel-body" >

    <div class="row">
	
		<div class="col-sm-2 form-group">
            <label>Tipologia</label>
			
			<select name="TYPE_ID" onchange="location.href='?${utils.remove(querystringParams,'TYPE_ID')}&TYPE_ID='+$(this).val()" class="form-control">
				<option value=""></option>
			<#list buffers.tipologie as tip >
				<option value="${tip.value}" ${ ((req.TYPE_ID!'') == tip.value)?string('selected','') }  } >$[facet.TYPE_ID.${tip.value}:${tip.value?lower_case?cap_first?replace('_',' ')}]</option>
			</#list>
			</select>
        </div>
		
		<div class="col-sm-3 form-group">
			<label>UO Principale</label>
			<input name="COD_UO" value="${req['COD_UO']!''}" data-tokenLimit="1" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=+type:group +GRUPPO_STRUTTURA:true" >
		</div>
		
		<div class="col-sm-3 form-group">
			<label>Classifica</label>
			<input name="CLASSIFICA" value="${req['CLASSIFICA']!''}" data-tokenLimit="1" class="auto-completion form-control" data-source="${context}/autocompletionService?fq=+type:titolario" >
		</div>
		
		<div class="col-sm-2 form-group">
            <label>Firmatario</label>
			<input name="FIRMATARIO_X"  value="${req['FIRMATARIO_X']!''}" data-tokenLimit="1" class="auto-completion2 form-control" data-source="${context}/query/solr-auto-facet?field=FIRMATARIO_X" />
        </div>
		
		<div class="col-sm-2 form-group">
            <label>#</label>
			<input name="DOCNUM" class="form-control" value="${req['DOCNUM']!''}" />
        </div>

	</div>
	
	<div class="row">
	
		<div class="col-sm-2 ">
			<div class="col-sm-10 form-group checkbox pull-right">
				<input name="-VERSIONE_SUPERATA" type="checkbox" ${ ( (req['-VERSIONE_SUPERATA']!'0')=="")?string('checked','') } value="0" >
				versioni superate
			</div>
		</div>
		
		<div class="col-sm-3 form-group">
            <label>Data documento da</label>
			<input name="DATA_DOCUMENTO[" type="date" value="${req['DATA_DOCUMENTO[']!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>a</label>
			<input name="DATA_DOCUMENTO]" type="date" value="${req['DATA_DOCUMENTO]']!''}" class="form-control">
        </div>
		
		<div class="col-sm-4 form-group">
            <label>Tipo componente</label>
			<br/>
			<span style="display: inline-block;vertical-align: top;padding: 6px;">
				<input name="TIPO_COMPONENTE" type="checkbox" value="PRINCIPALE" ${ ((lreq['TIPO_COMPONENTE']![])?seq_contains("PRINCIPALE"))?string('checked','') } }  >&nbsp;Principale&nbsp;
				<input name="TIPO_COMPONENTE" type="checkbox" value="ALLEGATO" ${ ((lreq['TIPO_COMPONENTE']![])?seq_contains("ALLEGATO"))?string('checked','') } } >&nbsp;Allegato&nbsp;
				<input name="TIPO_COMPONENTE" type="checkbox" value="ANNESSO" ${ ((lreq['TIPO_COMPONENTE']![])?seq_contains("ANNESSO"))?string('checked','') } } >&nbsp;Annesso&nbsp;
			</span>
        </div>
		
		
		
	</div>
	
	<#include  "tipologie/"+(req['TYPE_ID']!"NOT_FOUND")+"-search.ftl" ignore_missing=true  />
	
</div>

</div>

<div class="panel panel-default" >
<div class="panel-heading" >
	<button class="panel-title btn-link" type="button" data-toggle="collapse" data-target="#protCollapse" >
	  Dati di protocollazione
	</button>
</div>
<div id="protCollapse" class="panel-body collapse ${ (req.TIPO_PROTOCOLLAZIONE?? || req.OGGETTO_PG?? || req.NUM_PG?? )?string('in','' ) } " >

    <div class="row">
	
		<div class="col-sm-3 form-group">
            <label>Tipo</label>
			
			<select name="TIPO_PROTOCOLLAZIONE" class="form-control">
				<option value=""></option>
				<option value="E" ${ ((req['TIPO_PROTOCOLLAZIONE']!'') == 'E')?string('selected','') }  } >Entrata</option>
				<option value="I" ${ ((req['TIPO_PROTOCOLLAZIONE']!'') == 'I')?string('selected','') }  } >Interna</option>
				<option value="U" ${ ((req['TIPO_PROTOCOLLAZIONE']!'') == 'U')?string('selected','') }  } >Uscita</option>
			</select>
        </div>
		
		<div class="col-sm-7 form-group">
            <label>Oggetto</label>
			<input name="OGGETTO_PG" class="form-control" value="${req['OGGETTO_PG']!''}" />
        </div>
		
		<div class="col-sm-2 form-group">
            <label>#</label>
			<input name="NUM_PG" class="form-control" value="${req['NUM_PG']!''}" />
        </div>
		
		
	</div>
	<div class="row" >
		
		<div class="col-sm-3 form-group">
            <label>Data da</label>
			<input name="DATA_PG[" type="date" value="${req['DATA_PG_DA[']!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>a</label>
			<input name="DATA_PG]" type="date" value="${req['DATA_PG_A]']!''}" class="form-control">
        </div>
		
		<div class="col-sm-3 form-group">
            <label>Mittenti</label>
			<input name="MITTENTI_X" class="form-control" value="${req.MITTENTI_X!''}" />
        </div>
	
		<div class="col-sm-3 form-group">
            <label>Destinatari</label>
			<input name="DESTINATARI_X" class="form-control" value="${req.DESTINATARI_X!''}" />
        </div>
				
	</div>
</div>
</div>

<div class="panel panel-default" >
<div class="panel-heading" >
	<button class="panel-title btn-link" type="button" data-toggle="collapse" data-target="#regCollapse" >
	  Dati di Registrazione
	</button>
</div>
<div id="regCollapse" class="panel-body collapse ${ (req.ID_REGISTRO?? || req.OGGETTO_REGISTRAZ_T?? || req.N_REGISTRAZ?? )?string('in','' ) } " >

    <div class="row">
	
		<div class="col-sm-3 form-group">
            <label>Registro</label>
			
			<select name="ID_REGISTRO" class="form-control">
				<option value=""></option>
				<#list buffers.registri as reg >
				<option value="${reg.value}" ${ ((req.ID_REGISTRO!'') == reg.value)?string('selected','') }  } >$[facet.ID_REGISTRO.${reg.value}:${reg.value?lower_case?cap_first?replace('_',' ')}]</option>
				</#list>
			</select>
        </div>
		
		<div class="col-sm-7 form-group">
            <label>Oggetto</label>
			<input name="OGGETTO_REGISTRAZ_T" class="form-control" value="${req.OGGETTO_REGISTRAZ_T!''}" />
        </div>
		
		<div class="col-sm-2 form-group">
            <label>#</label>
			<input name="N_REGISTRAZ" class="form-control" value="${req.N_REGISTRAZ!''}" />
        </div>
		
		<div class="col-sm-3 form-group">
            <label>Data da</label>
			<input name="D_REGISTRAZ_DA[" type="date" value="${req['D_REGISTRAZ_DA[']!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>a</label>
			<input name="D_REGISTRAZ_A]" type="date" value="${req['D_REGISTRAZ_A]']!''}" class="form-control">
        </div>
				
	</div>
</div>

</div>

	
