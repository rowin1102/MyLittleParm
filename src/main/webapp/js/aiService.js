document.addEventListener('DOMContentLoaded', function () {
  var ctx = document.body?.dataset?.ctx || '';
  var map = { pretty:'PRETTY', easy:'EASY', resistant:'RESISTANT',
              repellent:'REPELLENT', interior:'INTERIOR', practical:'PRACTICAL' };
  Object.keys(map).forEach(function (id) {
    var el = document.getElementById(id);
    if (!el) return;
    el.addEventListener('click', function(){
      location.href = ctx + '/ai/plantRecommend.do?btn=' + map[id];
    });
  });
});


const fileInput = document.getElementById('pestImage');
  const preview   = document.getElementById('preview');
  const uploadBox = document.querySelector('.upload-box');


  // 드래그앤드롭
  ['dragenter','dragover'].forEach(evt =>
    uploadBox.addEventListener(evt, e => { e.preventDefault(); uploadBox.classList.add('dragover'); })
  );
  ['dragleave','drop'].forEach(evt =>
    uploadBox.addEventListener(evt, e => { e.preventDefault(); uploadBox.classList.remove('dragover'); })
  );
  uploadBox.addEventListener('drop', e => {
    if (e.dataTransfer.files?.length) {
      fileInput.files = e.dataTransfer.files;
      showPreview(fileInput.files[0]);
    }
  });

  // 파일 선택 시 미리보기
  fileInput.addEventListener('change', (e) => {
    if (e.target.files?.length) {
      showPreview(e.target.files[0]);
    }
  });

  function showPreview(file){
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      preview.src = e.target.result;
      uploadBox.classList.add('has-image');   // 안내문 숨김 + 이미지 표시
    };
    reader.readAsDataURL(file);
  }