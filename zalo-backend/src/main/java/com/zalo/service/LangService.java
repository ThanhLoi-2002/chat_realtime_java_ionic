package com.zalo.service;

import com.zalo.dto.filter.LangFilter;
import com.zalo.dto.request.Lang.LangCreationRequest;
import com.zalo.dto.request.Lang.LangUpdateRequest;
import com.zalo.dto.response.Lang.LangResponse;
import com.zalo.mapper.LangMapper;
import com.zalo.model.Lang;
import com.zalo.repository.LangRepository;
import com.zalo.repository.dto.LangDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LangService {

    LangRepository langRepository;
    LangMapper langMapper;

    Map<String, Map<String, String>> cache = new HashMap<>();

//    @PostConstruct
//    public void loadLang() {
//
//        List<Lang> list = langRepository.findAll();
//
//        for (Lang l : list) {
//
//            Map<String, String> value = new HashMap<>();
//            value.put("vi", l.getVi());
//            value.put("en", l.getEn());
//            value.put("tw", l.getTw());
//            value.put("cn", l.getCn());
//
//            cache.put(l.getCode(), value);
//        }
//    }

//    public String t(String code, String lang) {
//
//        Map<String, String> map = cache.get(code);
//
//        if (map == null) {
//            return code;
//        }
//
//        return map.getOrDefault(lang, map.get("vi"));
//    }

    public Map<String, String> getByLang(String lang) {
//        Map<String, String> result = new HashMap<>();
//
//        for (Map.Entry<String, Map<String, String>> entry : cache.entrySet()) {
//
//            String code = entry.getKey();
//            Map<String, String> value = entry.getValue();
//
//            result.put(code, value.getOrDefault(lang, value.get("vi")));
//        }

        return langRepository.findByLang(lang).stream()
                .collect(Collectors.toMap(
                        LangDto::getCode,
                        LangDto::getValue
                ));
    }

    public Page<LangResponse> getAll(LangFilter filter) {
//        List<Lang> list = langRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
//        return langMapper.toListResponses(list);

        Pageable pageable = filter.toPageable();

        Page<Lang> page = langRepository.findAllLang(filter.getCode(), pageable);

        return page.map(langMapper::toResponse);
    }

    public LangResponse getById(Long id) {
        return langMapper.toResponse(langRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound")));
    }

    public LangResponse create(LangCreationRequest lang, Long userId) {

        if (langRepository.existsByCode(lang.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Code already exists");
        }

        Lang e = langMapper.toEnity(lang);
        e.setCu(userId);

        LangResponse langResponse = langMapper.toResponse(langRepository.save(e));

//        loadLang();

        return langResponse;
    }

    public LangResponse update(Long id, LangUpdateRequest request, Long userId) {

        Lang lang = langRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        langMapper.updateDtoToEntity(lang, request);
        lang.setEu(userId);

        LangResponse langResponse = langMapper.toResponse(langRepository.save(lang));
//        loadLang();
        return langResponse;
    }

    public void delete(Long id) {
        langRepository.deleteById(id);
//        loadLang();
    }
}