// router/dynamicRouter.ts
import { StructureType } from '@/types/entities';
import { RouteRecordRaw } from 'vue-router';

// 1. Quét toàn bộ file .vue trong thư mục views
// Hãy thay đổi đường dẫn '../../views/**/*.vue' cho khớp với vị trí đặt file của bạn
const modules = import.meta.glob('../views/**/*.vue');
// console.log('Danh sách modules Vite quét được:', Object.keys(modules));

/**
 * Hàm đệ quy duyệt cây menu, lọc và chuyển đổi thành mảng phẳng các Route dạng Vue Router
 */
// export function generateRoutesFromMenu(menuList: StructureType[]): RouteRecordRaw[] {
//   const routes: RouteRecordRaw[] = [];

//   function traverse(items: StructureType[]) {
//     for (const item of items) {
//       // Điều kiện lọc: component có giá trị và kết thúc bằng đuôi .vue
//       if (item.component && item.component.endsWith('.vue')) {
        
//         // Chuẩn hóa đường dẫn tương đối tới file thực tế
//         // Ví dụ biến "Admin/System/User/User.vue" thành "../views/Admin/System/User/User.vue"
//         const componentPath = `../views/${item.component}`;

//         if (modules[componentPath]) {
//           routes.push({
//             path: item.path,
//             // name: item.code, // Hoặc kết hợp id để tránh trùng lặp name: `${item.code}_${item.id}`
//             component: modules[componentPath], // Lazy load component qua Vite glob
//             meta: {
//             //   title: item.code,
//             //   icon: item.icon,
//               permissions: item.permissions
//             }
//           });
//         } else {
//           console.warn(`[Router] Không tìm thấy file vật lý cho component: ${componentPath}`);
//         }
//       }

//       // Nếu có node con, tiếp tục đệ quy xuống dưới để tìm các node có đuôi .vue khác
//       if (item.children && item.children.length > 0) {
//         traverse(item.children);
//       }
//     }
//   }

//   traverse(menuList);
//   return routes;
// }

// mảng có phân cấp
export function generateRoutesFromMenu(menuList: StructureType[]): RouteRecordRaw[] {

  function buildRoutes(items: StructureType[]): RouteRecordRaw[] {
    return items
      .map(item => {
        const route: Partial<RouteRecordRaw> = {
          path: item.path,
          meta: {
            permissions: item.permissions
          }
        };

        // Nếu node là page
        if (item.component?.endsWith(".vue")) {
          const componentPath = `../views/${item.component}`;

          if (!modules[componentPath]) {
            console.warn(`[Router] Không tìm thấy ${componentPath}`);
            return null;
          }

          route.component = modules[componentPath];
        }

        // Có menu con
        if (item.children?.length) {
          route.children = buildRoutes(item.children);
        }

        // Nếu node không có component và cũng không có children thì bỏ
        if (!route.component && (!route.children || route.children.length === 0)) {
          return null;
        }

        return route;
      })
      .filter(Boolean) as RouteRecordRaw[];
  }

  return buildRoutes(menuList);
}