<?php
  require('conexao.php');
?>

<div class="card-header">

    <table class="table table-striped">
        <thead>
            <tr>
                <th>Nome</th>
                <th>Tipo</th>
                <th>Capacidade</th>
                <th>Status</th>
            </tr>
        </thead>
        <?php
            //VIEW DE AMBIENTES CAPACIDADE DESC
            $sql = "Select * from view_ambientes_capacidade"; 
            
            $res = $conn->query($sql);

            $qtd = $res->num_rows;



            if($qtd > 0){
                while($row = $res->fetch_object()){
               
        ?>
                    <tr>
                        <td><?=$row->nomeAmbiente?></td>
                        <td><?=$row->tipoAmbiente?></td>
                        <td><?=$row->capacidade?></td>
                        <td> <?php 
                        if($row->disponibilidade==1){
                            echo("Disponível");
                        }else{
                            echo("Reservado");
                        }?>    
                        </td>
                    </tr>
        <?php
         $_SESSION['idAmbiente'] = $row->idAmbiente;
                }
            }
        ?>
    </table>


</div>