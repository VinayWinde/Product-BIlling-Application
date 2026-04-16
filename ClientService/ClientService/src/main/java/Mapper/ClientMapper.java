package Mapper;


import DTO.ClientRequestDTO;
import DTO.ClientResponseDTO;
import Entity_clients.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    // 1. Convert Request DTO -> Entity (Used for Saving)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "purchases", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClientEntity toEntity(ClientRequestDTO dto);

    // 2. Convert Entity -> Response DTO (Used for API Output)
    // MapStruct will automatically map 'clientid' to 'clientId' if it recognizes the name
    // similarity, but we can be explicit to be safe.
    @Mapping(source = "clientId", target = "clientId")
    ClientResponseDTO toDTO(ClientEntity entity);
}
