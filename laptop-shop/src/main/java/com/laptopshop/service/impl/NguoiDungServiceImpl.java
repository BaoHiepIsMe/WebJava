package com.laptopshop.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.laptopshop.dto.TaiKhoanDTO;
import com.laptopshop.entities.NguoiDung;
import com.laptopshop.entities.VaiTro;
import com.laptopshop.repository.NguoiDungRepository;
import com.laptopshop.repository.VaiTroRepository;
import com.laptopshop.service.NguoiDungService;

@Service
@Transactional
public class NguoiDungServiceImpl implements NguoiDungService {

    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @Autowired
    private VaiTroRepository vaiTroRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public NguoiDung findByEmail(String email) {
        return nguoiDungRepo.findByEmail(email);
    }

    @Override
    public NguoiDung findByConfirmationToken(String confirmationToken) {
        return null;
    }

    @Override
    public NguoiDung saveUserForMember(NguoiDung nd) {
        // Kiểm tra email đã tồn tại chưa
        if (nguoiDungRepo.findByEmail(nd.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại.");
        }

        // Mã hóa mật khẩu
        nd.setPassword(bCryptPasswordEncoder.encode(nd.getPassword()));

        // Thiết lập vai trò người dùng
        Set<VaiTro> setVaiTro = new HashSet<>();
        setVaiTro.add(vaiTroRepo.findByTenVaiTro("ROLE_MEMBER"));
        nd.setVaiTro(setVaiTro);

        // Lưu người dùng
        return nguoiDungRepo.save(nd); // ID sẽ được auto-generate nếu là auto-increment
    }

    @Override
    public NguoiDung findById(long id) {
        return nguoiDungRepo.findById(id).orElse(null);
    }

    @Override
    public NguoiDung updateUser(NguoiDung nd) {
        return nguoiDungRepo.save(nd);
    }

    @Override
    public void changePass(NguoiDung nd, String newPass) {
        nd.setPassword(bCryptPasswordEncoder.encode(newPass));
        nguoiDungRepo.save(nd);
    }

    @Override
    public Page<NguoiDung> getNguoiDungByVaiTro(Set<VaiTro> vaiTro, int page) {
        return nguoiDungRepo.findByVaiTro(vaiTro, PageRequest.of(page - 1, 6));
    }

    @Override
    public List<NguoiDung> getNguoiDungByVaiTro(Set<VaiTro> vaiTro) {
        return nguoiDungRepo.findByVaiTro(vaiTro);
    }

    @Override
    public NguoiDung saveUserForAdmin(TaiKhoanDTO dto) {
        // Kiểm tra email đã tồn tại chưa
        if (nguoiDungRepo.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại.");
        }

        // Tạo người dùng mới từ DTO
        NguoiDung nd = new NguoiDung();
        nd.setHoTen(dto.getHoTen());
        nd.setDiaChi(dto.getDiaChi());
        nd.setEmail(dto.getEmail());
        nd.setSoDienThoai(dto.getSdt());
        nd.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

        // Thiết lập vai trò
        Set<VaiTro> vaiTro = new HashSet<>();
        vaiTro.add(vaiTroRepo.findByTenVaiTro(dto.getTenVaiTro()));
        nd.setVaiTro(vaiTro);

        return nguoiDungRepo.save(nd); // ID sẽ được auto-increment
    }

    @Override
    public void deleteById(long id) {
        nguoiDungRepo.deleteById(id);
    }
}
