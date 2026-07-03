package eu.gridverse.srv.rsa.services.workflow;
import eu.gridverse.data.cnm.rsa.RsaWorkflowState; import eu.gridverse.infra.metadata.MetadataRepository; import java.sql.Timestamp; import java.util.*; import org.springframework.jdbc.core.JdbcTemplate;
public final class JdbcWorkflowMetadataRepository implements MetadataRepository<WorkflowMetadata,UUID> {
    private final JdbcTemplate jdbc; public JdbcWorkflowMetadataRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
    private void schema(){jdbc.execute("create table if not exists rsa_workflow (id uuid primary key, network_id varchar(128) not null, state varchar(32) not null, updated_network_id varchar(128), updated_at timestamptz not null)");}
    public WorkflowMetadata save(WorkflowMetadata value){schema();jdbc.update("insert into rsa_workflow(id,network_id,state,updated_network_id,updated_at) values (?,?,?,?,?) on conflict(id) do update set network_id=excluded.network_id,state=excluded.state,updated_network_id=excluded.updated_network_id,updated_at=excluded.updated_at",value.id(),value.networkId(),value.state().name(),value.updatedNetworkId(),Timestamp.from(value.updatedAt()));return value;}
    public Optional<WorkflowMetadata> findById(UUID id){schema();return jdbc.query("select id,network_id,state,updated_network_id,updated_at from rsa_workflow where id=?",(rs,row)->new WorkflowMetadata(rs.getObject("id",UUID.class),rs.getString("network_id"),RsaWorkflowState.valueOf(rs.getString("state")),rs.getString("updated_network_id"),rs.getTimestamp("updated_at").toInstant()),id).stream().findFirst();}
}
