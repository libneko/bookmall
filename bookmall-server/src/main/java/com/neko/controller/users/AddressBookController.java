package com.neko.controller.users;

import com.neko.context.BaseContext;
import com.neko.entity.AddressBook;
import com.neko.result.Result;
import com.neko.service.AddressBookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    private final AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    @PostMapping
    public Result<Object> save(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }

    @PutMapping
    public Result<Object> update(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.update(addressBook);
        return Result.success();
    }

    @PutMapping("/default")
    public Result<Object> setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    @DeleteMapping
    public Result<Object> deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        //SQL: select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(true);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.getFirst());
        }

        return Result.error("没有查询到默认地址");
    }
}
