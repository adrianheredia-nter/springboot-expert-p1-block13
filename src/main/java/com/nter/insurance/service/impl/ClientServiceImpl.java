package com.nter.insurance.service.impl;

import com.nter.insurance.constants.AppConstants;
import com.nter.insurance.dto.ClientRequest;
import com.nter.insurance.dto.ClientResponse;
import com.nter.insurance.entity.ClientJPA;
import com.nter.insurance.exception.AlreadyExistsException;
import com.nter.insurance.exception.InvalidDataException;
import com.nter.insurance.exception.NotFoundException;
import com.nter.insurance.mapper.ClientMapper;
import com.nter.insurance.repository.ClientRepository;
import com.nter.insurance.service.ClientService;
import com.nter.insurance.utils.DniUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream().map(clientMapper::toResponse).toList();
    }

    @Override
    public ClientResponse findById(Integer idClient) {
        return clientMapper.toResponse(getClient(idClient));
    }

    @Override
    @Transactional
    public ClientResponse create(ClientRequest request) {
        validateDni(request.dni());
        if (clientRepository.existsById(request.idClient())) {
            throw new AlreadyExistsException(AppConstants.CLIENT_ALREADY_EXISTS.formatted(request.idClient()));
        }
        String dni = DniUtils.normalize(request.dni());
        if (clientRepository.existsByDniIgnoreCase(dni)) {
            throw new AlreadyExistsException(AppConstants.DNI_ALREADY_EXISTS.formatted(dni));
        }
        ClientJPA saved = clientRepository.save(clientMapper.toEntity(request));
        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClientResponse update(Integer idClient, ClientRequest request) {
        validateDni(request.dni());
        ClientJPA client = getClient(idClient);
        String dni = DniUtils.normalize(request.dni());
        if (!dni.equals(client.getDni()) && clientRepository.existsByDniIgnoreCase(dni)) {
            throw new AlreadyExistsException(AppConstants.DNI_ALREADY_EXISTS.formatted(dni));
        }
        client.setDni(dni);
        client.setName(request.name().trim());
        client.setSurname(request.surname().trim());
        client.setBirthdate(request.birthdate());
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional
    public void delete(Integer idClient) {
        clientRepository.delete(getClient(idClient));
    }

    private ClientJPA getClient(Integer idClient) {
        return clientRepository.findById(idClient)
                .orElseThrow(() -> new NotFoundException(AppConstants.CLIENT_NOT_FOUND.formatted(idClient)));
    }

    private void validateDni(String dni) {
        if (!DniUtils.isValid(dni)) {
            throw new InvalidDataException(AppConstants.INVALID_DNI.formatted(dni));
        }
    }
}
