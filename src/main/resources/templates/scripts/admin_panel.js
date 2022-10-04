// методы для работы с админкой через rest.

// У слова async один простой смысл: эта функция всегда возвращает промис. Значения других типов оборачиваются
// в завершившийся успешно промис автоматически.

// полезно для тестирования
function getRandomInt(max) {
    return Math.floor(Math.random() * max);
}


// структура для хранения данных пользователя
let user = {
    "id": null,
    "firstName": "",
    "lastName": "",
    "email": "",
    "password": "",
    "age": 0,
    "roles": [ "" ]
};

// рабочая. Пример работы ниже.
// user.email = getRandomInt(1000) + user.email;
// user.id = 2;
// user.roles[0] = "ADMIN";
// user.roles[2] = "USER";
// user.roles[4] = "ADMIN";
// updateUser(user).then(a => alert(JSON.stringify(a, null, 2)));
async function updateUser(user) {
    let response = await fetch('http://localhost:8080/rest/juser', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(user)
    });
    if (!response.ok) {
        alert("FAIL: " + response.status)
        let errMsg = await response.json();
        alert("updateUser() error: " + errMsg.error);
        return null;
    } else {
        return await response.json();
    }
}

// рабочая
// deleteUser(45).then(a => alert(a));
async function deleteUser(id) {
    // для разнообразия и опыта сделано через элемент формы. 
    let formData = new FormData();
    formData.append('id', id);

    let response = await fetch('http://localhost:8080/rest/user', {
        method: 'DELETE',
        // правльный 'Content-Type' в этом случае подставляется в headers автоматом, 
        // ничего дополнительно делать не нужно! 
        // headers: {
        //     'Content-Type': 'form/multipart'
        // },
        body: formData
    });
    if (!response.ok) {
        alert("FAIL: " + response.status)
        let msg = await response.json();
        alert("deleteUser() error: " + msg.error);
        return msg.error;
    } else {
        return null;
    }
}

// рабочая
// использование:  listAll().then(a => alert("listAll() ->" + a[1].email));
async function listAll() {
    let response = await fetch('http://localhost:8080/rest/');

    if (response.ok) { // если HTTP-статус в диапазоне 200-299
        let list = await response.json();
        return list;
    } else {
        alert("listALL() FAIL: " + response.status + " - " + response.statusText);
        return null;
    }
}

// рабочая
// использование: getUser(1).then(a => alert(a.email));
async function getUser(id) {
    let response = await fetch(`http://localhost:8080/rest/${id}`);
    if (!response.ok) {
        alert("FAIL: " + response.status)
        let msg = await response.json();
        alert("getUser(): " + msg.error);
        return null;
    } else {
        return await response.json();
    }
}


// рабочая
async function createUser(user) {
    let response = await fetch('http://localhost:8080/rest/juser', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(user)
    });

    let new_user = await response.json();
    return new_user;
}
//////////////////////////////////////////////////////////////////


// function listAll() {
//     let url = `http://localhost:8080/rest/`;
//
//     let response = fetch(url).then(
//         successResponse => {
//             if (successResponse.status != 200) {
//                 alert("ListAll() WARN: " + successResponse.status + " - " + successResponse.statusText);
//                 return null;
//             } else {
//                 let users = successResponse.json();
//                 alert("OK: " + users);
//                 return users;
//             }
//         },
//         failResponse => {
//             alert("listAll() FAIL: " + failResponse.status + " - " + failResponse.text());
//             return null;
//         }
//     );
// }

