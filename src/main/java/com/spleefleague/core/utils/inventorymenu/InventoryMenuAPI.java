package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogButtonTemplateBuilder;
import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogHolderTemplateBuilder;
import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogItemTemplateBuilder;
import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogTemplateBuilder;

public class InventoryMenuAPI {

    public static InventoryMenuTemplateBuilder menu() {
        return new InventoryMenuTemplateBuilder();
    }

    public static InventoryMenuItemTemplateBuilder item() {
        return new InventoryMenuItemTemplateBuilder();
    }
    
    public static <B> InventoryMenuDialogTemplateBuilder<B> dialog(Class<B> b) {
        return dialog();
    }
    
    public static <B> InventoryMenuDialogHolderTemplateBuilder<B> dialogMenu(Class<B> b) {
        return dialogMenu();
    }
    
    public static <B> InventoryMenuDialogButtonTemplateBuilder<B> dialogButton(Class<B> b) {
        return dialogButton();
    }
    
    public static <B> InventoryMenuDialogItemTemplateBuilder<B> dialogItem(Class<B> b) {
        return dialogItem();
    }
    
    public static <B> InventoryMenuDialogTemplateBuilder<B> dialog() {
        return new InventoryMenuDialogTemplateBuilder<>();
    }
    
    public static <B> InventoryMenuDialogHolderTemplateBuilder<B> dialogMenu() {
        return new InventoryMenuDialogHolderTemplateBuilder<>();
    }
    
    public static <B> InventoryMenuDialogButtonTemplateBuilder<B> dialogButton() {
        return new InventoryMenuDialogButtonTemplateBuilder<>();
    }
    
    public static <B> InventoryMenuDialogItemTemplateBuilder<B> dialogItem() {
        return new InventoryMenuDialogItemTemplateBuilder<>();
    }
}
