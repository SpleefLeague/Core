package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogItemTemplateBuilder;
import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogHolderTemplateBuilder;
import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogTemplateBuilder;

public class InventoryMenuAPI {

    public static InventoryMenuTemplateBuilder menu() {
        return new InventoryMenuTemplateBuilder();
    }

    public static InventoryMenuItemTemplateBuilder item() {
        return new InventoryMenuItemTemplateBuilder();
    }
    
    public static <B> InventoryMenuDialogTemplateBuilder<B> dialog(Class<B> b) {
        return new InventoryMenuDialogTemplateBuilder<>();
    }
    
    public static <B> InventoryMenuDialogHolderTemplateBuilder<B> dialogMenu(Class<B> b) {
        return new InventoryMenuDialogHolderTemplateBuilder<>();
    }
    
    public static <B> InventoryMenuDialogItemTemplateBuilder<B> dialogItem(Class<B> b) {
        return new InventoryMenuDialogItemTemplateBuilder<>();
    }
}
